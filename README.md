Gestión Móvil de Oportunidades y Activaciones Corporativas
Asignatura: DSY1105 - Desarrollo de Aplicaciones Móviles
Evaluación: Parcial 2 (Encargo)
Integrantes:

Joaquin sepulveda
Marcelo Moyano 

 Descripción del Proyecto
Este proyecto es una aplicación móvil nativa para Android desarrollada en Kotlin con Jetpack Compose. Su objetivo es resolver la problemática de una agencia de marketing directo, la cual necesita centralizar, priorizar y gestionar de manera anticipada las oportunidades de activación en eventos corporativos, municipales y ferias.

La aplicación permite el registro de oportunidades, la asignación de responsables y el seguimiento de estados, utilizando distintos niveles de acceso según el rol del usuario.

🚀 Funcionalidades Implementadas
1. Gestión de Roles y Navegación
Login validado: Sistema de autenticación con credenciales ficticias.

Navegación Dinámica: Uso de NavController para dirigir a cada usuario a un panel distinto según su cargo (Ejecutivo, Supervisor, Marketing, Directivo).

Vistas Diferenciadas:

Ejecutivo: Panel con contadores personales, botón de crear oportunidad y ver sus gestiones.

Supervisor: Panel global, opciones para ver todas las oportunidades y auditar gestiones atrasadas.

Marketing: Vista de eventos confirmados para preparar material.

Directivo: Reporte general de estados (Aceptadas, En Proceso, Rechazadas).

2. Módulo de Oportunidades
Lista Detallada: Visualización de tarjetas con información completa (tipo de evento, prioridad, potencial comercial, fuente, fechas y observaciones).

Control de Permisos:

Solo el Supervisor puede ver el menú de opciones para Asignar responsable o Descartar una oportunidad.

Solo el perfil de Marketing puede ver el botón para agregar requerimientos de material a eventos confirmados.

Los Ejecutivos solo visualizan sus oportunidades asignadas.

3. Formularios y Validaciones
Formularios completos para el registro y edición de oportunidades.

Validaciones lógicas manejadas a través de ViewModel (separadas de la UI).

Retroalimentación visual (mensajes de error en rojo, indicadores de carga).

 Arquitectura y Tecnologías
El proyecto sigue los lineamientos exigidos en la asignatura:

Lenguaje: Kotlin.

Interfaz: Jetpack Compose + Material Design 3.

Patrón de Arquitectura: MVVM (Model-View-ViewModel) para separar la lógica de negocio de la interfaz gráfica.

Gestión de Estado: mutableStateOf y StateFlow para la reactividad de la UI.

 Instrucciones para Ejecutar
Clona este repositorio: git clone [URL_DEL_REPOSITORIO]

Abre el proyecto en Android Studio (versión recomendada: Hedgehog o superior).

Espera a que Gradle sincronice las dependencias.

Ejecuta la aplicación en un emulador o dispositivo físico con Android 8.0 (API 26) o superior.

 Credenciales de Prueba
Puedes probar los diferentes roles utilizando las siguientes credenciales ficticias:

Rol	Usuario	Contraseña
Ejecutivo	ejecutivo@agencia.cl	1234
Supervisor	supervisor@agencia.cl	1234
Marketing	marketing@agencia.cl	1234
Directivo	admin	123

Planificación y Trabajo Colaborativo
Tablero Trello: [Pega aquí el enlace a tu tablero de Trello]

El desarrollo se dividió en tareas de diseño de UI, lógica de ViewModels, navegación y validaciones.

