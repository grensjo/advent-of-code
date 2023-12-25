package aockt.y2023

import io.github.jadarma.aockt.core.Solution

class UnionFind(private val n: Int) {
    private val parent: MutableList<Int> = MutableList(n) { i -> i }
    private val rank: MutableList<Int> = MutableList(n) { 0 }

    fun find(u: Int) : Int {
        return if (parent[u] == u) {
            u
        } else {
            parent[u] = find(parent[u])
            parent[u]
        }
    }

    fun union(u: Int, v: Int) {
        var uu = find(u)
        var vv = find(v)
        if (uu != vv) {
            if (rank[uu] < rank[vv]) {
                val tmp = uu
                uu = vv
                vv = tmp
            }
            parent[vv] = uu
            if (rank[vv] == rank[uu]) rank[uu]++
        }
    }
}

object Y2023D25 : Solution {
    data class Edge(val u: Int, val v: Int)

    class TestCase(input: String) {
        private val labelToId: MutableMap<String, Int> = mutableMapOf()

        private val edges = input.lineSequence().flatMap { line ->
            line.split(':').map(String::trim).let {parts ->
                val u = getId(parts[0])
                parts[1].split(' ').map(String::trim).map(::getId).map {v ->
                    Edge(u, v)
                }
            }
        }.toList()

        private fun getId(label: String) : Int {
            if (label !in labelToId) labelToId[label] = labelToId.size
            return labelToId.getValue(label)
        }

        // Finds a cut of size three using Karger's algorithm for minimum cut.
        fun solve() : Int? {
            var n = labelToId.size
            val uf = UnionFind(n)

            val shuffledEdges = edges.shuffled()
            for (edge in shuffledEdges) {
                if (n <= 2) break
                val set1 = uf.find(edge.u)
                val set2 = uf.find(edge.v)
                if (set1 != set2) {
                    n -= 1
                    uf.union(edge.u, edge.v)
                }
            }

            val componentSizes = labelToId.values.map { uf.find(it) }.groupingBy { it }.eachCount().toMap()
            assert(componentSizes.size == 2) { "Expected only two graph components, got ${componentSizes.size}" }

            val cutSize = edges.count { uf.find(it.u) != uf.find(it.v) }
            if(cutSize != 3) {
                // Did not succeed in finding a cut of size 3.
                return null
            }

            return componentSizes.values.reduce(Int::times)
        }
    }


    override fun partOne(input: String) : Int {
        val testCase = TestCase(input)
        var i = 0
        while (true) {
            val res = testCase.solve()
            i++
            if (res != null) {
                println("Solved after $i tries.")
                return res.also { println(it) }
            }
        }
    }
}
