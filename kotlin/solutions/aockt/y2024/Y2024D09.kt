package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D09 : Solution {

    data class DiskRegion(val pos: Int, val length: Int, val fileId: Int? = null)

    private fun parseInput(input: String): Pair<MutableList<Int?>, MutableSet<DiskRegion>> {
        val disk: MutableList<Int?> = mutableListOf()
        val diskRegions: MutableSet<DiskRegion> = mutableSetOf()
        var fileNumber = 0

        for ((i, ch) in input.strip().toList().withIndex()) {
            if (i % 2 == 0) {
                diskRegions.add(DiskRegion(disk.size, ch.digitToInt(), fileNumber))
                disk.addAll((0..<ch.digitToInt()).toList().map{ fileNumber })
                fileNumber++
            } else {
                diskRegions.add(DiskRegion(disk.size, ch.digitToInt(), null))
                disk.addAll((0..<ch.digitToInt()).toList().map { null })
            }
        }

        return disk to diskRegions
    }

    private fun List<Int?>.checksum(): Long =
        withIndex().sumOf { (i, fileId) -> if (fileId != null) { i.toLong() * fileId.toLong() } else { 0 } }

    override fun partOne(input: String): Long {
        val (disk, _) = parseInput(input)

        var emptySpot = disk.indexOf(null)
        var lastBlock = disk.indexOfLast { it != null }

        while (lastBlock > emptySpot) {
            val tmp = disk[lastBlock]
            disk[emptySpot] = tmp
            disk[lastBlock] = null

            emptySpot += disk.subList(emptySpot + 1, disk.size).indexOf(null) + 1
            lastBlock = disk.subList(0, lastBlock).indexOfLast { it != null }
        }

        return disk.checksum()
            .also { println(it) }
    }

    override fun partTwo(input: String): Long {
        val (disk, regions) = parseInput(input)

        val files = regions.filter { it.fileId != null }.sortedByDescending { it.fileId }

        for (file in files) {
            val freeSpace = regions.filter { it.fileId == null && it.pos < file.pos && it.length >= file.length }
                .minByOrNull { it.pos }
            if (freeSpace == null) { continue }

            for (i in file.pos..<(file.pos + file.length)) {
                disk[i] = null
            }
            for (i in freeSpace.pos..<(freeSpace.pos + file.length)) {
                disk[i] = file.fileId
            }
            regions.remove(freeSpace)
            if (freeSpace.length > file.length) {
                regions.add(DiskRegion(freeSpace.pos + file.length, freeSpace.length - file.length, null))
            }
        }

        return disk.checksum()
            .also { println(it) }
    }
}
