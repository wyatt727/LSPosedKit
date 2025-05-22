package com.wobbz.module.networkguard.rules

data class NetworkRule(
    val id: String,
    val action: String, // "ALLOW" or "BLOCK"
    val target: String, // URL or domain pattern
    val description: String = "",
    val enabled: Boolean = true
) 