# Turismo Madrid

### Introducción
Este proyecto es una aplicación backend para la gestión y consulta de alojamientos turísticos en la Comunidad de Madrid, a partir de los Datos Abiertos (DA) oficiales. 

### Stack Tecnológico
* **Java 21+**: Se utilizan algunas de las últimas características del lenguaje. Recomendamos correr con GraalVM para un mejor rendimiento.
* **Spring Boot** 3.5.0.
* **MongoDB** 
* **Wiremock**: Mocking de servicios externos (DA) para pruebas de integración.
* **Maven**

### Novedades
* **Clases selladas (_sealed classes_)**: Permiten definir jerarquías de clases restringidas al conocerse exactamente la estructura de los datos, mejorando la seguridad y mantenibilidad del modelo de dominio (por ejemplo, [AlojamientoTuristico](src/main/java/com/wadajo/turismomadrid/domain/model/AlojamientoTuristico.java) y sus subtipos).
* **Pattern matching**: Simplifica la lógica de control y desestructuración de objetos, especialmente útil en el manejo de diferentes tipos de alojamientos turísticos. Gran complemento a las clases selladas, [por ejemplo aquí](src/main/java/com/wadajo/turismomadrid/domain/service/TurismoService.java#L100).
* **Threads virtuales**: Ejecución de tareas asíncronas, lo que permite un manejo más eficiente de la concurrencia y simplifica el código asíncrono. [por ejemplo aquí](src/main/java/com/wadajo/turismomadrid/domain/service/TurismoService.java#L96).