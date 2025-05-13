[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/-R5QJ2C8)
# Fallboot

![Logo](logo.png)

Fallboot es un simple y liviano framework para crear aplicaciones web
en Java. Está diseñado para ser fácil de usar y rápido de aprender,
lo que lo convierte en una excelente opción para desarrolladores que
buscan una solución sencilla para crear aplicaciones web.

## Funcionalidades de fall

- Soporte para recibir peticiones HTTP
- Soporte para serializar y deserializar JSON
- Soporte para deserializar YAML
- Soporte para generar ids únicos

## TODOs app

Su tarea es implementar, con lo que nos provee el framework, una aplicación
que permita gestionar una lista de tareas. La aplicación debe poder recibir
peticiones HTTP para crear, leer, actualizar y eliminar tareas. Las peticiones
que deberan soportar seran las siguientes:

- GET /tasks: Devuelve la lista de tareas
- POST /tasks: Crea una nueva tarea
- GET /tasks/{id}: Devuelve una tarea por su id
- PUT /tasks/{id}: Actualiza una tarea por su id
- DELETE /tasks/{id}: Elimina una tarea por su id

Cada tarea, serializada a JSON, debe tener la siguiente forma:

```json
{
  "id": "c81265d3-93b7-4740-bcfc-59810f662f41",
  "title": "Tarea 1",
  "description": "Descripcion de la tarea 1",
  "done": false
}
```

Todos los campos son obligatorios. Los campos `title`, `description` y `done` son editables.
El campo `id` es un campo autogenerado, por lo que no debe ser enviado en la peticion de creacion.

### Persistencia

Para la persistencia de las tareas, se debe utilizar una base de datos basada en archivos. La
estructura de la base de datos es la siguiente:

```
tasks/
  ├── c81265d3-93b7-4740-bcfc-59810f662f41.json
  ├── 2f3b4e5c-1a2b-4d3e-8f7c-9a0b1c2d3e4f.json
  ├── 4f5e6d7c-8a9b-4c3e-8f7c-9a0b1c2d3e4f.json
```

Donde cada archivo JSON representa una tarea. El nombre del archivo es el id de la tarea.

### Logging

La aplicacion debe logear en distintos niveles de severidad:

Como INFO:

- Cuando se recibe una peticion
- Cuando se devuelve una respuesta

Como ERROR:

- Cuando se produce un error

Como DEBUG:

- Cuando se crea una tarea, indicando el id de la tarea y los campos de la tarea
- Cuando se actualiza una tarea, indicando el id de la tarea y los campos de la tarea
- Cuando se elimina una tarea, la tarea eliminada

El destingo de cada uno de esos niveles de severidad se determina en el archivo `src/main/resources/configuration.yml`
que se encuentra en el proyecto. Un ejemplo del archivo de configuracion de logs es el siguiente:

```yaml
loggers:
  - level: INFO
    transports:
      - console
      - file
  - level: ERROR
    transports:
      - file
  - level: DEBUG
    transports:
      - console
```

Los `transports` hacen referencia a donde se va a guardar/enviar el contenido de los logs, `console` es enviarlos al
"standard output" del proceso (`System.out.println` en Java) y `file` agregarlos secuencialmente al final de un file (
Este se puede definir libremente).Los distintos `transports` para cada nivel de severidad pueden se pueden definir.

### Testing

Se deberan completar los casos de prueba que se encuentran en la suite de pruebas. Se debe tener en cuenta que los tests
no deben escribir enviar logs al "standard output" del proceso ni escribir contenido en archivos. 