# Neocities Live Resource Manager
An application designed to monitor a file in a specific directory, check for changes, and, if there has been a change, upload the file to Neocities.

## Compiling
### Prerequisites
- Java 17 or higher

### How to compile the application
Open your terminal in the application folder and type:

```bash
javac *.java
```

This will compile all the Java files in the directory.

## Usage
Open your terminal in the application folder and type `java neolrm` to run the application. You can also use the following commands to auto-complete some fields:

### Arguments for Execution
You can run the application with different combinations of arguments:

1. Full execution (complete all fields):
    ```bash
    java neolrm <Neocities username> <password> <source file path> <target file path>
    ```
    - **Neocities username**: Your Neocities account username.
    - **password**: Your Neocities account password.
    - **source file path**: Full path to the file you want to monitor and upload.
    - **target file path**: Full path to where the file will be uploaded on Neocities.
2. Partial execution (missing the target file path):
    ```bash
    java neolrm <Neocities username> <password> <source file path>
    ```
3. Minimal execution (missing the source file path and target file path):
    ```bash
    java neolrm <Neocities username> <password>
    ```
4. Just the username (missing everything else):
    ```bash
    java neolrm <Neocities username> 
    ```
In these cases, the application will ask for the missing fields in a command line interface.

## Settings
The program settings can be changed in the `config.properties` file. These settings affect how the program behaves.

### Default settings
```properties
#Neocities Live Resource Manager
#Fri Mar 07 17:03:44 BRT 2025
log_binaries=1
loop_time=60000
log_events=1

```

### All settings
| Full name        | Type           | Available options    | Default value      | Description                                                                            |
|------------------|----------------|----------------------|--------------------|----------------------------------------------------------------------------------------|
| `history_user`   | String         | Any text             | _Empty_            | The last Neocities username inserted into the name field                               |
| `history_source` | String         | Any text             | _Empty_            | The last, local, file path inserted into the source field                              |
| `history_target` | String         | Any text             | _Empty_            | The last, in cloud, file path inserted into the target field                           |
| `loop_time`      | Signed Integer | Any positive integer | 60000 (one minute) | The amount of time, in millis, the program should wait before uploading into neocities |
| `log_events`     | Boolean        | 0, 1                 | 1                  | Determines if the program should log terminal outputs                                  |
| `log_binaries`   | Boolean        | 0, 1                 | 1                  | Determines if the program should log every binary file it uploads to Neocities         |

