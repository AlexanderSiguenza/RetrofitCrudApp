package sv.edu.udb.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActualizarAlumnoActivity : AppCompatActivity() {

    private lateinit var api: AlumnoApi
    private var alumno: Alumno? = null

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var actualizarButton: Button

    // Obtener las credenciales de autenticación
    val auth_username = "admin"
    val auth_password = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_alumno)

        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        edadEditText = findViewById(R.id.edadEditText)
        actualizarButton = findViewById(R.id.actualizarButton)


        // Crea una instancia de Retrofit con el cliente OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl("https://66240d4504457d4aaf9b8530.mockapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Crea una instancia del servicio que utiliza la autenticación HTTP básica
        val api = retrofit.create(AlumnoApi::class.java)

        // Obtener el ID del alumno de la actividad anterior
        val alumnoId = intent.getIntExtra("alumno_id", -1)
        Log.e("API", "alumnoId : $alumnoId")

        val nombre = intent.getStringExtra("nombre").toString()
        val apellido = intent.getStringExtra("apellido").toString()
        val edad = intent.getIntExtra("edad", 1)

        nombreEditText.setText(nombre)
        apellidoEditText.setText(apellido)
        edadEditText.setText(edad.toString())

        val alumno = Alumno(0,nombre, apellido, edad)

        // Configurar el botón de actualización
        actualizarButton.setOnClickListener {
            if (alumno != null) {
                // Crear un nuevo objeto Alumno con los datos actualizados
                val alumnoActualizado = Alumno(
                    alumnoId,
                    nombreEditText.text.toString(),
                    apellidoEditText.text.toString(),
                    edadEditText.text.toString().toInt()
                )
                //Log.e("API", "alumnoActualizado : $alumnoActualizado")

                val jsonAlumnoActualizado = Gson().toJson(alumnoActualizado)
                Log.d("API", "JSON enviado: $jsonAlumnoActualizado")

                val gson = GsonBuilder()
                    .setLenient() // Agrega esta línea para permitir JSON malformado
                    .create()

                // Realizar una solicitud PUT para actualizar el objeto Alumno
                api.actualizarAlumno(alumnoId, alumnoActualizado).enqueue(object : Callback<Alumno> {
                    override fun onResponse(call: Call<Alumno>, response: Response<Alumno>) {
                        if (response.isSuccessful && response.body() != null) {
                            // Si la solicitud es exitosa, mostrar un mensaje de éxito en un Toast
                            Toast.makeText(this@ActualizarAlumnoActivity, "Alumno actualizado correctamente", Toast.LENGTH_SHORT).show()
                            val i = Intent(getBaseContext(), MainActivity::class.java)
                            startActivity(i)
                        } else {
                            // Si la respuesta del servidor no es exitosa, manejar el error
                            try {
                                val errorJson = response.errorBody()?.string()
                                val errorObj = JSONObject(errorJson)
                                val errorMessage = errorObj.getString("message")
                                Toast.makeText(this@ActualizarAlumnoActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                // Si no se puede parsear la respuesta del servidor, mostrar un mensaje de error genérico
                                Toast.makeText(this@ActualizarAlumnoActivity, "Error al actualizar el alumno", Toast.LENGTH_SHORT).show()
                                Log.e("API", "Error al parsear el JSON: ${e.message}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<Alumno>, t: Throwable) {
                        // Si la solicitud falla, mostrar un mensaje de error en un Toast
                        Log.e("API", "onFailure : $t")
                        Toast.makeText(this@ActualizarAlumnoActivity, "Error al actualizar el alumno", Toast.LENGTH_SHORT).show()

                        // Si la respuesta JSON está malformada, manejar el error
                        try {
                            val gson = GsonBuilder().setLenient().create()
                            val error = t.message ?: ""
                            val alumno = gson.fromJson(error, Alumno::class.java)
                            // trabajar con el objeto Alumno si se puede parsear
                        } catch (e: JsonSyntaxException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        } catch (e: IllegalStateException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        }
                    }
                })
            }
        }
    }
}