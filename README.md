# TravelPlus

Este repositorio contiene el código fuente para TravelPlus, una aplicación basada en Java diseñada para la gestión de rutas de transporte, usuarios y viajes. El proyecto implementa una arquitectura Cliente-Servidor y utiliza estructuras de datos personalizadas para la gestión eficiente de la información.



## Descripción

TravelPlus es un sistema que permite modelar y gestionar una red de transporte. El núcleo del sistema se basa en una implementación propia de estructuras de datos (Grafos y Listas Enlazadas) para representar paraderos y rutas, evitando la dependencia excesiva de las colecciones estándar de Java para fines de aprendizaje y control granular.

El sistema está respaldado por una base de datos MySQL robusta que gestiona usuarios, administradores, historiales de viajes y la topología de la red de transporte.

## Características Principales

- **Gestión de Usuarios y Administradores:** Tablas separadas para clientes y administradores con credenciales seguras y estados de cuenta (activo/bloqueado).

- **Sistema de Rutas y Paraderos:** Modelado de rutas con distancias, tiempos y direcciones completas.

- Estructuras de Datos Personalizadas:

	- ```Graph:``` Implementación de grafo ponderado no dirigido para conectar paraderos.

	- ```LinkedList & CircularList:``` Implementaciones propias de listas para el manejo de colecciones.

- **Base de Datos Optimizada:** Esquema SQL (```travelplus```) que utiliza ```BIGINT``` para timestamps Unix y relaciones de integridad referencial.

- **Arquitectura Cliente-Servidor:** Separación lógica entre la interfaz del cliente y la lógica del servidor.

## Estructura del Proyecto

El código está organizado en los siguientes paquetes dentro de ```src/```:

- ```client```: Lógica de la aplicación cliente.

- ```server```: Lógica del servidor y procesamiento de peticiones.

- ```common```: Estructuras de datos compartidas (```Graph```, ```LinkedList```, ```Edge```, ```Vertex```, ```ListNode```).

- ```objects```: Clases de dominio que representan las entidades del negocio (```User```, ```Route```, ```Stop```).

- ```db```: Gestión de la conexión a la base de datos.

## Configuración de la Base de Datos

El script de generación se encuentra en ```sql/DB.sql```. Este script está diseñado para:

1. Eliminar el esquema ```travelplus``` existente (si lo hay) para asegurar un estado limpio.

2. Generar las tablas: ```usuario```, ```administrador```, ```paradero```, ```ruta```, ```viaje```, ```amigo```, ```historial_busqueda```, y ```estadistica_ruta```.

3. Configurar índices únicos y claves foráneas.

## Requisitos Previos

- Java Development Kit (JDK)

- MySQL Server

- Visual Studio Code (o cualquier IDE de Java)


