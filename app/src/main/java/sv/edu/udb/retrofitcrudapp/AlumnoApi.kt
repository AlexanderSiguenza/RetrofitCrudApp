package sv.edu.udb.retrofitcrudapp

import retrofit2.Call
import retrofit2.http.*

interface AlumnoApi {

    @GET("escuela/alumno")
    fun obtenerAlumnos(): Call<List<Alumno>>

    @GET("escuela/alumno/{id}")
    fun obtenerAlumnoPorId(@Path("id") id: Int): Call<Alumno>

    @POST("escuela/alumno")
    fun crearAlumno(@Body alumno: Alumno): Call<Alumno>

    @PUT("escuela/alumno/{id}")
    fun actualizarAlumno(@Path("id") id: Int, @Body alumno: Alumno): Call<Alumno>

    @DELETE("escuela/alumno/{id}")
    fun eliminarAlumno(@Path("id") id: Int): Call<Void>
}