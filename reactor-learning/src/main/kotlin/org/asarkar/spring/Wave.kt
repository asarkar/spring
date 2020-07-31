package org.asarkar.spring

import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WaveHeader(bytes: ByteArray) {
    init {
        require(bytes.size >= SIZE) { "Input size is must be at least $SIZE bytes" }
    }

    private var start = 0
    private val riff = RiffChunk(
        String(bytes.copyOfRange(start, start + 4))
            .also {
                require(it == "RIFF") { "$it must be 'RIFF'" }
                start += it.length
            },
        ByteBuffer.wrap(bytes.copyOfRange(start, start + 4)).order(ByteOrder.LITTLE_ENDIAN)
            .also { start += it.capacity() }.int,
        String(bytes.copyOfRange(start, start + 4))
            .also {
                require(it == "WAVE") { "$it must be 'WAVE'" }
                start += it.length
            }
    )
    private val format = FormatChunk(
        // null terminated
        String(bytes.copyOfRange(start, start + 3))
            .also {
                require(it == "fmt") { "$it must be 'fmt'" }
                start += 4
            },
        ByteBuffer.wrap(bytes.copyOfRange(start, start + 4)).order(ByteOrder.LITTLE_ENDIAN)
            .also { start += it.capacity() }.int,
        ByteBuffer.wrap(bytes.copyOfRange(start, start + 2)).order(ByteOrder.LITTLE_ENDIAN)
            .also { start += it.capacity() }
            .let { if (it.short == 1.toShort()) "PCM" else "OTHER (${it.short})" },
        ByteBuffer.wrap(bytes.copyOfRange(start, start + 2)).order(ByteOrder.LITTLE_ENDIAN)
            .also { start += it.capacity() }.short,
        ByteBuffer.wrap(bytes.copyOfRange(start, start + 4)).order(ByteOrder.LITTLE_ENDIAN)
            .also { start += it.capacity() }.int,
        ByteBuffer.wrap(bytes.copyOfRange(start, start + 4)).order(ByteOrder.LITTLE_ENDIAN)
            .also { start += it.capacity() }.int,
        ByteBuffer.wrap(bytes.copyOfRange(start, start + 2)).order(ByteOrder.LITTLE_ENDIAN)
            .also { start += it.capacity() }.short,
        ByteBuffer.wrap(bytes.copyOfRange(start, start + 2)).order(ByteOrder.LITTLE_ENDIAN)
            .also { start += it.capacity() }.short
    )
    private val `data` = DataChunk(
        String(bytes.copyOfRange(start, start + 4))
             // remove all null chars
            .replace("\u0000", "")
            .also { start += 4 },
        ByteBuffer.wrap(bytes.copyOfRange(start, start + 4)).order(ByteOrder.LITTLE_ENDIAN)
            .also { start += it.capacity() }.int
    )

    init {
        check(start == 44) { "Expected to have read 44 bytes but read $start" }
    }

    data class RiffChunk(val id: String, val size: Int, val format: String)
    data class FormatChunk(
        val id: String, val size: Int, val format: String, val numChannels: Short,
        val sampleRate: Int, val byteRate: Int, val blockAlign: Short, val bitsPerSample: Short
    )

    data class DataChunk(val id: String, val size: Int)

    override fun toString(): String {
        val ls = System.lineSeparator()
        return "WaveHeader($ls\t$riff}$ls\t$format$ls\t$`data`$ls)"
    }

    companion object {
        const val SIZE = 44

        fun fromPath(path: String): WaveHeader  = fromInputStream(WaveHeader::class.java.getResourceAsStream(path))

        fun fromUrl(url: String): WaveHeader  = fromInputStream(URL(url).openStream())

        private fun fromInputStream(input: InputStream): WaveHeader {
            val bytes = input.use {
                it.readNBytes(SIZE)
            }
            return WaveHeader(bytes)
        }
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Argument is missing")
    }
    println(WaveHeader.fromUrl(args[0]))
}