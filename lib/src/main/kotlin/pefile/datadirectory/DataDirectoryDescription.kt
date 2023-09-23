package pefile.datadirectory

data class DataDirectoryDescription(val type: DataDirectoryType, val virtualAddress: Int, val rawAddress: Int, val size: Int)
