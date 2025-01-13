package com.alfredoguerrero.contacts.domain.usecases

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class UseCase<in Params, out Result>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default // Dispatcher por defecto
) {

    // Función principal que debe implementar la lógica del caso de uso
    protected abstract suspend fun execute(input: Params): Result

    // Método público para ejecutar el caso de uso en un `suspend function`
    suspend operator fun invoke(input: Params): Result = withContext(dispatcher) {
        execute(input)
    }

    // Método adicional para ejecutar en el hilo principal sin `suspend`
    fun executeOnMain(input: Params, callback: (Result) -> Unit) {
        // Cambia a Dispatchers.Main para ejecutar en el hilo principal
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(dispatcher) { execute(input) }
            callback(result)
        }
    }
}