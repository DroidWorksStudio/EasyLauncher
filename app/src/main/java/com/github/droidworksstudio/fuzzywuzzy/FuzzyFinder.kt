package com.github.droidworksstudio.fuzzywuzzy

import com.github.droidworksstudio.launcher.data.entities.AppInfo
import java.text.Normalizer
import java.util.*

object FuzzyFinder {
    fun scoreApp(app: AppInfo, searchChars: String, topScore: Int): Int {
        val appChars = app.appName

        val fuzzyScore = calculateFuzzyScore(
            normalizeString(appChars),
            normalizeString(searchChars)
        )

        return (fuzzyScore * topScore).toInt()
    }

    fun normalizeString(appLabel: String, searchChars: String): Boolean {
        return (appLabel.contains(searchChars, true) or
                Normalizer.normalize(appLabel, Normalizer.Form.NFD)
                    .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
                    .replace(Regex("[-_+,. ]"), "")
                    .contains(searchChars, true))
    }

    private fun normalizeString(input: String): String {
        // Remove diacritical marks and special characters, and convert to uppercase
        return input
            .uppercase(Locale.getDefault())
            .replace(Regex("[\\p{InCombiningDiacriticalMarks}-_+,.]"), "")
    }

    private fun calculateFuzzyScore(s1: String, s2: String): Float {
        val m = s1.length
        val n = s2.length
        var matchCount = 0
        var s1Index = 0

        // Iterate over each character in s2 and check if it exists in s1
        for (c2 in s2) {
            var found = false

            // Start searching for c2 from the current s1Index
            for (j in s1Index until m) {
                if (s1[j] == c2) {
                    found = true
                    // Update s1Index to the next position for the next iteration
                    s1Index = j + 1
                    break
                }
            }

            // If the current character in s2 is not found in s1, return a score of 0
            if (!found) {
                return 0f
            }

            // Increment the match count
            matchCount++
        }

        // Calculate the score as the ratio of matched characters to the longer string length
        return matchCount.toFloat() / maxOf(m, n)
    }
}
