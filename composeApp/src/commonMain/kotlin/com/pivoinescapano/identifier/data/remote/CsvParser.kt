package com.pivoinescapano.identifier.data.remote

import com.pivoinescapano.identifier.data.model.FieldEntry

class CsvParser {
    fun parseFieldEntries(
        csvContent: String,
        columnMapping: Map<String, String>,
        headerRowIndex: Int = 0,
    ): List<FieldEntry> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.size <= headerRowIndex) return emptyList()

        val headers = parseCsvLine(lines[headerRowIndex])
        val columnIndices = buildColumnIndices(headers, columnMapping)

        return lines
            .drop(headerRowIndex + 1)
            .mapNotNull { line ->
                parseFieldEntryRow(line, columnIndices)
            }
    }

    private fun buildColumnIndices(
        headers: List<String>,
        mapping: Map<String, String>,
    ): Map<String, Int> {
        val reversedMapping = mapping.entries.associate { (csvColumn, property) -> property to csvColumn }

        return reversedMapping.mapNotNull { (property, csvColumn) ->
            val index =
                headers.indexOfFirst {
                    it.trim().equals(csvColumn, ignoreCase = true)
                }
            if (index >= 0) property to index else null
        }.toMap()
    }

    private fun parseFieldEntryRow(
        line: String,
        columnIndices: Map<String, Int>,
    ): FieldEntry? {
        val values = parseCsvLine(line)

        val champ = columnIndices["champ"]?.let { values.getOrNull(it)?.trim() }
        val parcel = columnIndices["parcel"]?.let { values.getOrNull(it)?.trim() }
        val rang = columnIndices["rang"]?.let { values.getOrNull(it)?.trim() }
        val trou = columnIndices["trou"]?.let { values.getOrNull(it)?.trim() }

        if (champ.isNullOrBlank() || parcel.isNullOrBlank() || rang.isNullOrBlank() || trou.isNullOrBlank()) {
            return null
        }

        return FieldEntry(
            champ = champ,
            parcel = parcel,
            rang = rang,
            trou = trou,
            variety = columnIndices["variety"]?.let { values.getOrNull(it)?.trim() },
            yearPlanted = columnIndices["yearPlanted"]?.let { values.getOrNull(it)?.trim() },
            size = columnIndices["size"]?.let { values.getOrNull(it)?.trim() },
            etiquette = columnIndices["etiquette"]?.let { values.getOrNull(it)?.trim() },
            forSale = columnIndices["forSale"]?.let { values.getOrNull(it)?.trim() },
        )
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val currentValue = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(currentValue.toString())
                    currentValue.clear()
                }

                else -> currentValue.append(char)
            }
        }

        result.add(currentValue.toString())

        return result
    }
}
