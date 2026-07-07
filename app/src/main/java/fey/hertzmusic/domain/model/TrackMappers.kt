package fey.hertzmusic.domain.model

fun List<Track>.toFolders(): List<Folder> =
    asSequence()
        .filter { it.path.isNotEmpty() }
        .groupBy { it.path.substringBeforeLast('/') }
        .map { (dir, tracks) ->
            Folder(
                name = dir.removePrefix("/storage/emulated/0/").ifEmpty { "/" },
                path = dir,
                tracks = tracks,
            )
        }
        .sortedBy { it.name.lowercase() }

fun List<Track>.toAlbums(): List<Album> =
    groupBy { it.album }
        .map { (name, tracks) ->
            val artists = tracks.map { it.artist }.distinct()
            Album(
                name = name,
                artist = artists.singleOrNull() ?: "various artists",
                tracks = tracks.sortedBy { it.trackNumber },
            )
        }
        .sortedBy { it.name.lowercase() }

fun List<Track>.toArtists(): List<Artist> =
    run {
        val allRawArtists = map { it.artist }.distinct()
        val splitDelimiters = listOf(", ", "; ", " & ")

        val individualArtists = allRawArtists.flatMap { raw ->
            splitDelimiters.fold(listOf(raw)) { acc, delimiter ->
                acc.flatMap { it.split(delimiter) }
            }
        }.map { it.trim() }.filter { it.isNotEmpty() }.toSet()

        val knownIndividuals = individualArtists.filter { individual ->
            allRawArtists.any { raw -> raw.equals(individual, ignoreCase = true) }
        }.toSet()

        flatMap { track ->
            val raw = track.artist
            val parts = splitDelimiters.fold(listOf(raw)) { acc, delimiter ->
                acc.flatMap { it.split(delimiter) }
            }.map { it.trim() }.filter { it.isNotEmpty() }

            if (parts.size > 1) {
                val knownParts = parts.filter { p ->
                    knownIndividuals.any { it.equals(p, ignoreCase = true) }
                }

                if (knownParts.isNotEmpty()) {
                    knownParts.map { p ->
                        val matchedName = knownIndividuals.find { it.equals(p, ignoreCase = true) } ?: p
                        // Use the original raw artist string for the grouping but under the known artist name
                        matchedName to track
                    }
                } else {
                    listOf(raw to track)
                }
            } else {
                listOf(raw to track)
            }
        }
            .groupBy({ it.first }, { it.second })
            .map { (name, tracks) ->
                Artist(
                    name = name,
                    tracks = tracks.distinctBy { it.id }.sortedBy { it.title.lowercase() },
                )
            }
            .sortedBy { it.name.lowercase() }
    }
