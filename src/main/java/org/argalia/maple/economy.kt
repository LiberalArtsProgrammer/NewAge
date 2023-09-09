package org.argalia.maple

import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

fun getPlayerMoney(player: Player): Int {
    val persistentDataContainer = player.persistentDataContainer
    return persistentDataContainer.getOrDefault(
            Maple.moneyKey,
            PersistentDataType.INTEGER,
            0
    )
}

fun setPlayerMoney(player: Player, amount: Int) {
    val persistentDataContainer = player.persistentDataContainer
    persistentDataContainer.set(Maple.moneyKey, PersistentDataType.INTEGER, amount)
}

fun increasePlayerMoney(player: Player, amount: Int) {
    val currentMoney = getPlayerMoney(player)
    val newMoney = currentMoney + amount
    setPlayerMoney(player, newMoney)
    player.sendMessage("Money increased by $amount. New balance: $newMoney")
}

fun decreasePlayerMoney(player: Player, amount: Int) {
    val currentMoney = getPlayerMoney(player)
    if (currentMoney >= amount) {
        val newMoney = currentMoney - amount
        setPlayerMoney(player, newMoney)
        player.sendMessage("Money decreased by $amount. New balance: $newMoney")
    } else {
        player.sendMessage("Insufficient funds. You only have $currentMoney.")
    }
}


fun showPlayerMoney(player: Player) {
    val money = getPlayerMoney(player)
    player.sendMessage("Your money: $money")
}