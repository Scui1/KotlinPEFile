package pefile.datadirectory

enum class DataDirectoryType {
    EXPORT_DIRECTORY,
    IMPORT_DIRECTORY,
    RESOURCE_DIRECTORY,
    EXCEPTION_DIRECTORY,
    SECURITY_DIRECTORY,
    BASE_RELOCATION_TABLE,
    DEBUG_DIRECTORY,
    ARCHITECTURE_SPECIFIC_DATA,
    RVA_OF_GLOBALPTR,
    TLS_DIRECTORY,
    LOAD_CONFIGURATION_DIRECTORY,
    BOUNDARY_IMPORT_DIRECTORY,
    IMPORT_ADDRESS_TABLE,
    DELAY_LOAD_IMPORT_DESCRIPTORS,
    DOTNET_HEADER
}