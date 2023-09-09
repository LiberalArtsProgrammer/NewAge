import org.argalia.maple.Maple
import org.argalia.maple.decreasePlayerMoney
import org.argalia.maple.getPlayerMoney
import org.argalia.maple.showPlayerMoney
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

fun starforce(item: ItemStack, player: Player): ItemStack {
    if(getPlayerMoney(player)>10000) {
        decreasePlayerMoney(player, 10000)
        val meta = item.itemMeta ?: return item.clone()
        val lore = meta.lore ?: mutableListOf()
        val key = NamespacedKey(JavaPlugin.getPlugin(Maple::class.java), "starforce")
        val value = PersistentDataType.INTEGER
        val currentValue = meta.persistentDataContainer.get(key, value) ?: 0

        val successarr = arrayOf(100, 90, 80, 70, 60, 50, 40, 30, 20, 10)
        val failarr = arrayOf(0, 10, 20, 30, 30, 30, 30, 30, 40, 10)
        val downarr = arrayOf(0, 0, 0, 0, 10, 20, 20, 30, 30, 50)
        val breakarr = arrayOf(0, 0, 0, 0, 0, 0, 10, 10, 10, 30)

        if (lore.size == 0) {
            lore.add("${ChatColor.GRAY}이 아이템은 특별한 힘이 담겨있다.")
            lore.add("${ChatColor.GOLD}☆☆☆☆☆☆☆☆☆☆")
            lore.add("${ChatColor.GREEN}성공확률: 100%")
            lore.add("${ChatColor.BLUE}실패확률: 0%")
            lore.add("${ChatColor.RED}하락확률: 0%")
            lore.add("${ChatColor.DARK_RED}파괴확률: 0%")
        } else if( lore.size < 3 ){
            val laterCurrentValue = meta.persistentDataContainer.get(key, value) ?: 0
            lore.add("${ChatColor.GREEN}성공확률: ${successarr[laterCurrentValue]}%")
            lore.add("${ChatColor.BLUE}실패확률: ${failarr[laterCurrentValue]}%")
            lore.add("${ChatColor.RED}하락확률: ${downarr[laterCurrentValue]}%")
            lore.add("${ChatColor.DARK_RED}파괴확률: ${breakarr[laterCurrentValue]}%")
        } else {
            when (rand(currentValue)) {
                1 -> {
                    val increasedValue = currentValue + 1
                    meta.persistentDataContainer.set(key, value, increasedValue)

                    val starCount = minOf(increasedValue, 10)
                    lore[1] = "${ChatColor.GOLD}${"★".repeat(starCount)}${ChatColor.GRAY}${"☆".repeat(10 - starCount)}"
                }
                2 -> {
                    player.sendMessage("실패!")
                }
                3 -> {
                    player.sendMessage("아이쿠! 손이 미끄러졌네요!")
                    // 실패 시 다운그레이드 로직
                    if (currentValue > 0) {
                        val decreasedValue = currentValue - 1
                        meta.persistentDataContainer.set(key, value, decreasedValue)
                        val starCount = maxOf(decreasedValue, 0)
                        lore[1] = "${ChatColor.GOLD}${"★".repeat(starCount)}${ChatColor.GRAY}${"☆".repeat(10 - starCount)}"
                    }
                }
                4 -> {
                    player.closeInventory()
                    player.sendMessage("수준ㅋㅋㅋ")
                    player.inventory.setItemInMainHand(null)
                }
            }
            val laterCurrentValue = meta.persistentDataContainer.get(key, value) ?: 0
            lore[2] = "${ChatColor.GREEN}성공확률: ${successarr[laterCurrentValue]}%"
            lore[3] = "${ChatColor.BLUE}실패확률: ${failarr[laterCurrentValue]}%"
            lore[4] = "${ChatColor.RED}하락확률: ${downarr[laterCurrentValue]}%"
            lore[5] = "${ChatColor.DARK_RED}파괴확률: ${breakarr[laterCurrentValue]}%"
        }

        item.itemMeta = meta
        val reinforcedItem = item.clone()
        val reinforcedMeta = reinforcedItem.itemMeta
        reinforcedMeta.lore = lore
        reinforcedItem.itemMeta = reinforcedMeta
        return reinforcedItem
    } else {
        player.closeInventory()
        player.sendMessage("돈이 없어요...")
        return item
    }
}


fun rand(k : Int): Int {
    val random = Random()
    val num = random.nextInt(100)
    val successarr = arrayOf(100, 90, 80, 70, 60, 50, 40, 30, 20, 10)
    val failarr = arrayOf(0, 100, 100, 100, 90, 80, 70, 60, 60, 20)
    val downarr = arrayOf(0, 0, 0, 0, 100, 100, 90, 90, 90, 70)
    if (num <= successarr[k]) {
        return 1
    } else if (successarr[k] < num && num <= failarr[k]) {
        return 2
    } else if (failarr[k] < num && num <= downarr[k]) {
        return 3
    } else {
        return 4
    }
}

fun magicStarforce(item: ItemStack, player: Player) {
    if(item.type.name.endsWith("_CHESTPLATE")){
        giveHealthArmor(item)
    }
}

private fun giveHealthArmor(item: ItemStack): ItemStack {
    val healthArmor = ItemStack(Material.DIAMOND_CHESTPLATE)
    val meta = healthArmor.itemMeta

    // 갑옷의 이름 설정
    meta.setDisplayName("생명의 갑옷")

    // Attribute를 사용하여 추가 체력 설정
    val attributeModifier = AttributeModifier(UUID.randomUUID(), "health_armor", 100.0, AttributeModifier.Operation.ADD_NUMBER,  EquipmentSlot.CHEST)
    meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, attributeModifier)

    healthArmor.itemMeta = meta

    return healthArmor
}