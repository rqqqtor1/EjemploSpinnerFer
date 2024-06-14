package bryan.miranda.ejemplospinner

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassDoctores
import java.util.Calendar
import java.util.UUID

class pacientes : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_pacientes, container, false)
        val spDoctores = root.findViewById<Spinner>(R.id.spDoctores)
        val txtFechaNacimiento = root.findViewById<EditText>(R.id.txtFechaNacimiento)
        val txtNombrePaciente = root.findViewById<EditText>(R.id.txtNombrePaciente)
        val txtDireccionPaciente = root.findViewById<EditText>(R.id.txtDireccionPaciente)
        val btnGuardarPaciente = root.findViewById<Button>(R.id.btnGuardarPaciente)

        fun obtenerDoctores(): List<dataClassDoctores> {
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resulSet = statement?.executeQuery("select * from tbDoctores")!!

            val ListaDoctores = mutableListOf<dataClassDoctores>()

            while (resulSet.next()){
                val uuid = resulSet.getString("DoctorUUID")
                val nombre = resulSet.getString("nombreDoctor")
                val especialidad = resulSet.getString("Especialidad")
                val telefono = resulSet.getString("Telefono")
                val unDoctorCompleto = dataClassDoctores(uuid, nombre, especialidad, telefono)
                ListaDoctores.add(unDoctorCompleto)
            }
            return ListaDoctores
        }

        CoroutineScope(Dispatchers.IO).launch {
            val ListadeDoctores = obtenerDoctores()
            val nombreDoctores = ListadeDoctores.map { it.nombreDoctor }

            //Crear adaptador
withContext(Dispatchers.Main) {
    val miAdaptador = ArrayAdapter(
        requireContext(),
        android.R.layout.simple_spinner_dropdown_item,
        nombreDoctores
    )
    spDoctores.adapter = miAdaptador
}
        }

        //Mostrar el calendario al hacer click en el EditText txtFechaNacimientoPaciente
        txtFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada =
                        "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtFechaNacimiento.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )
            datePickerDialog.show()
        }
//programar boton de guardar
        btnGuardarPaciente.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                //Crear un obj de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()

                val doctor = obtenerDoctores()

            //Crear una variable que contenga un PrepateStatement
                val addPaciente = objConexion?.prepareStatement("insert into tbPacientes(PacienteUUID, DoctorUUID, Nombre, FechaNacimiento, Direccion) values (?, ?, ?, ?, ?)")!!
                addPaciente.setString(1, UUID.randomUUID().toString())
                addPaciente.setString(2, doctor[spDoctores.selectedItemPosition].DoctorUUID)
                addPaciente.setString(3, txtNombrePaciente.text.toString())
                addPaciente.setString(4, txtFechaNacimiento.text.toString() )
                addPaciente.setString(5, txtDireccionPaciente.text.toString())
                addPaciente.executeUpdate()

                withContext(Dispatchers.Main){
                    txtNombrePaciente.setText("")
                    txtDireccionPaciente.setText("")
                    txtFechaNacimiento.setText("")
                    Toast.makeText(requireContext(), "paciente agregado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return root
    }
}