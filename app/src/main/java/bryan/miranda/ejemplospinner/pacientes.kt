package bryan.miranda.ejemplospinner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassDoctores

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


        return root
    }
}