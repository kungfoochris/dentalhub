package com.abhiyantrik.dentalhub.models

data class FlagModifyDelete(
    val id: Long,
    val encounter: ModifyEncounter,
    val reason_for_modification: String,
    val modify_status: String,
    val reason_for_deletion: String,
    val other_reason_for_deletion: String,
    val delete_status: String,
    val flag: String = "",
    val modify_approved_at: String
)



