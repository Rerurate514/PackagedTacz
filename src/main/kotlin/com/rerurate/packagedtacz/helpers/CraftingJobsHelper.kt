package com.rerurate.packagedtacz.helpers

import appeng.api.networking.IGrid
import appeng.api.stacks.AEKey
import appeng.api.storage.AEKeyFilter
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

class CraftingJobsHelper {

    /**
     * グリッド上で現在クラフトが要求されているすべてのアイテムと、その要求量を取得する。
     * * @param grid 情報を取得する対象のAE2グリッド。
     * @return AEKeyをキーとし、その要求量を値とするMap。
     */
    fun getCraftingJobs(grid: IGrid): Map<AEKey, Long> {
        val craftingService = grid.craftingService ?: return Collections.emptyMap()
        val allCraftables = craftingService.getCraftables(AEKeyFilter.none())
        val jobs = ConcurrentHashMap<AEKey, Long>()
        for (key in allCraftables) {
            val requestedAmount = craftingService.getRequestedAmount(key)

            if (requestedAmount > 0) {
                jobs[key] = requestedAmount
            }
        }

        return jobs
    }

    fun formatCraftingJobsForLog(jobs: Map<AEKey, Long>): String {
        if (jobs.isEmpty()) {
            return "No active crafting jobs."
        }

        val formattedString = StringBuilder("Active Crafting Jobs:\n")
        jobs.forEach { (key, amount) ->
            formattedString.append("- ${key.displayName.string}: $amount\n")
        }
        return formattedString.toString()
    }
}