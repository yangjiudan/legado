package io.legado.app.help

import io.legado.app.data.entities.ReplaceRule
import io.legado.app.utils.*

object ReplaceAnalyzer {

    fun jsonToReplaceRules(json: String): List<ReplaceRule> {
        val replaceRules = mutableListOf<ReplaceRule>()
        val items: List<Map<String, Any>> = jsonPath.parse(json).read("$")
        for (item in items) {
            val jsonItem = jsonPath.parse(item)
            jsonToReplaceRule(jsonItem.jsonString())?.let {
                if (it.isValid()) {
                    replaceRules.add(it)
                }
            }
        }
        return replaceRules
    }

    private fun jsonToReplaceRule(json: String): ReplaceRule? {
        val replaceRule: ReplaceRule? = GSON.fromJsonObject<ReplaceRule>(json.trim()).getOrNull()
        runCatching {
            if (replaceRule == null || replaceRule.pattern.isBlank()) {
                val jsonItem = jsonPath.parse(json.trim())
                val rule = ReplaceRule()
                rule.id = jsonItem.readLong("$.id") ?: System.currentTimeMillis()
                rule.pattern = jsonItem.readString("$.regex") ?: ""
                if (rule.pattern.isEmpty()) return null
                rule.name = jsonItem.readString("$.replaceSummary") ?: ""
                rule.replacement = jsonItem.readString("$.replacement") ?: ""
                rule.isRegex = jsonItem.readBool("$.isRegex") == true
                rule.scope = jsonItem.readString("$.useTo")
                rule.isEnabled = jsonItem.readBool("$.enable") == true
                rule.order = jsonItem.readInt("$.serialNumber") ?: 0
                return rule
            }
        }
        return replaceRule
    }

}