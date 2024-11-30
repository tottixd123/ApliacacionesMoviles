package com.example.gemerador;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gemerador.Crear_Ti.Crear_nuevo_ti;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE, sdk = 33)
public class Crear_nuevo_tiTest {
    private static final String PREFS_NAME = "TicketPrefs";
    private static final String TICKET_COUNTER_KEY = "ticketCounter";
    private Crear_nuevo_ti crearNuevoTiActivity;

    @Mock private SharedPreferences sharedPreferences;
    @Mock private SharedPreferences.Editor editor;
    @Mock private Context context;
    @Mock private Spinner problemSpinner;
    @Mock private Spinner areaSpinner;
    @Mock private Spinner prioritySpinner;
    @Mock private EditText problemDetailEditText;
    @Mock private Editable editable;
    @Mock private FirebaseAuth firebaseAuth;
    @Mock private FirebaseDatabase firebaseDatabase;
    @Mock private DatabaseReference databaseReference;
    @Mock private com.google.android.gms.tasks.Task<DataSnapshot> mockTask;
    @Mock private DataSnapshot mockSnapshot;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock estáticos necesarios
        mockStatic(FirebaseAuth.class);
        mockStatic(FirebaseDatabase.class);

        when(FirebaseAuth.getInstance()).thenReturn(firebaseAuth);
        when(FirebaseDatabase.getInstance()).thenReturn(firebaseDatabase);

        // Mock para Firebase Database
        when(firebaseDatabase.getReference(anyString())).thenReturn(databaseReference);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);

        // Mock para el Task de Firebase
        when(databaseReference.get()).thenReturn(mockTask);
        when(mockTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            com.google.android.gms.tasks.OnCompleteListener listener = invocation.getArgument(0);
            when(mockTask.isSuccessful()).thenReturn(true);
            when(mockTask.getResult()).thenReturn(mockSnapshot);
            when(mockSnapshot.child(anyString())).thenReturn(mockSnapshot);
            when(mockSnapshot.getValue(String.class)).thenReturn("Test User");
            listener.onComplete(mockTask);
            return mockTask;
        });

        // Crear actividad usando Robolectric
        crearNuevoTiActivity = Robolectric.setupActivity(Crear_nuevo_ti.class);

        // Configurar los mocks después de crear la actividad
        setPrivateField(crearNuevoTiActivity, "problemSpinner", problemSpinner);
        setPrivateField(crearNuevoTiActivity, "areaSpinner", areaSpinner);
        setPrivateField(crearNuevoTiActivity, "prioritySpinner", prioritySpinner);
        setPrivateField(crearNuevoTiActivity, "problemDetailEditText", problemDetailEditText);
        setPrivateField(crearNuevoTiActivity, "mAuth", firebaseAuth);
        setPrivateField(crearNuevoTiActivity, "sharedPreferences", sharedPreferences);

        // Configurar comportamiento de los mocks
        when(sharedPreferences.edit()).thenReturn(editor);
        when(sharedPreferences.getInt(eq(TICKET_COUNTER_KEY), anyInt())).thenReturn(0);
        when(editor.putInt(eq(TICKET_COUNTER_KEY), anyInt())).thenReturn(editor);

        // Mock del usuario de Firebase
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(firebaseAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getUid()).thenReturn("testUID");

        // Configurar los spinners
        when(problemSpinner.getSelectedItem()).thenReturn("Test Problem");
        when(areaSpinner.getSelectedItem()).thenReturn("Test Area");
        when(prioritySpinner.getSelectedItem()).thenReturn("Alta");
        when(problemSpinner.getSelectedItemPosition()).thenReturn(1);
        when(areaSpinner.getSelectedItemPosition()).thenReturn(1);
        when(prioritySpinner.getSelectedItemPosition()).thenReturn(1);

        when(problemDetailEditText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn("Test Details");
    }

    @Test
    public void testSendTicket() throws Exception {
        // Ejecutar el looper principal para procesar tareas pendientes
        shadowOf(getMainLooper()).idle();

        // Simular clic en el botón
        crearNuevoTiActivity.findViewById(R.id.sendTicketButton).performClick();

        // Esperar a que se procesen las operaciones asíncronas
        shadowOf(getMainLooper()).idle();

        // Verificar las interacciones
        verify(prioritySpinner, atLeastOnce()).getSelectedItemPosition();
        verify(problemSpinner, atLeastOnce()).getSelectedItemPosition();
        verify(areaSpinner, atLeastOnce()).getSelectedItemPosition();
        verify(databaseReference, atLeastOnce()).get();
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}