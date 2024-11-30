package com.example.gemerador;

import static org.junit.Assert.*;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.gemerador.Crear_Ti.Crear_nuevo_ti;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class Crear_nuevo_tiTest {

    private Crear_nuevo_ti activity;
    private Spinner problemSpinner;
    private Spinner areaSpinner;
    private EditText problemDetailEditText;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(Crear_nuevo_ti.class).create().get();
        problemSpinner = activity.findViewById(R.id.problemSpinner);
        areaSpinner = activity.findViewById(R.id.areaSpinner);
        problemDetailEditText = activity.findViewById(R.id.problemDetailEditText);
    }

    @Test
    public void testInitialSpinnerSelection() {
        assertEquals(0, problemSpinner.getSelectedItemPosition());
        assertEquals(0, areaSpinner.getSelectedItemPosition());
    }

    @Test
    public void testValidateInput_EmptyFields() throws Exception {
        Method validateInputMethod = Crear_nuevo_ti.class.getDeclaredMethod("validateInput");
        validateInputMethod.setAccessible(true);

        problemSpinner.setSelection(0);
        areaSpinner.setSelection(0);
        problemDetailEditText.setText("");

        assertFalse((Boolean) validateInputMethod.invoke(activity));
    }

    @Test
    public void testValidateInput_ValidFields() throws Exception {
        Method validateInputMethod = Crear_nuevo_ti.class.getDeclaredMethod("validateInput");
        validateInputMethod.setAccessible(true);

        problemSpinner.setSelection(1);
        areaSpinner.setSelection(1);
        problemDetailEditText.setText("Test problem details");

        assertTrue((Boolean) validateInputMethod.invoke(activity));
    }

    @Test
    public void testTicketCounterIncrement() throws Exception {
        Field ticketCounterField = Crear_nuevo_ti.class.getDeclaredField("ticketCounter");
        ticketCounterField.setAccessible(true);
        int initialCounter = (int) ticketCounterField.get(activity);

        Method incrementTicketCounterMethod = Crear_nuevo_ti.class.getDeclaredMethod("incrementTicketCounter");
        incrementTicketCounterMethod.setAccessible(true);
        incrementTicketCounterMethod.invoke(activity);

        assertEquals(initialCounter + 1, (int) ticketCounterField.get(activity));
    }

    @Test
    public void testUpdateTicketCounter() throws Exception {
        Field ticketCounterField = Crear_nuevo_ti.class.getDeclaredField("ticketCounter");
        ticketCounterField.setAccessible(true);
        ticketCounterField.set(activity, 42);

        Method updateTicketCounterMethod = Crear_nuevo_ti.class.getDeclaredMethod("updateTicketCounter");
        updateTicketCounterMethod.setAccessible(true);
        updateTicketCounterMethod.invoke(activity);

        TextView ticketCounterTextView = activity.findViewById(R.id.ticketCounterTextView);
        assertEquals("Ticket-C042", ticketCounterTextView.getText().toString());
    }
}