package org.argalia.maple

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

fun getMoney(player: Player): Int { // 플레이어로부터 돈를 가져오는 함수
    return player.persistentDataContainer.get(Maple.moneyKey, PersistentDataType.INTEGER) ?: 0
}

fun giveMoney(player: Player, value: Int) { // 플레이어에게 돈를 변경하는 함수
    player.persistentDataContainer.set(Maple.moneyKey, PersistentDataType.INTEGER, getMoney(player) + value)
}

fun getStar(item: ItemStack): Int { //데이터 가져오기
    val meta: ItemMeta = item.itemMeta ?: return 0
    return meta.persistentDataContainer.getOrDefault(Maple.starKey, PersistentDataType.INTEGER, 0)
}

fun giveStar(item: ItemStack, value: Int): ItemStack { //데이터 바꾸기
    val meta = item.itemMeta ?: return item.clone()

    meta.persistentDataContainer.set(Maple.starKey, PersistentDataType.INTEGER,  getStar(item) + value)
    item.itemMeta = meta

    return item
}