package fey.hertzmusic.domain.model

data class DmtStats(val totalMs: Long = 0L, val counts: Map<Long, Int> = emptyMap())
