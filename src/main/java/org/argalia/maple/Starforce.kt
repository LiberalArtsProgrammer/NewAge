import org.argalia.maple.Maple
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

fun starforce(item: ItemStack, player: Player): ItemStack {
    val meta = item.itemMeta ?: return item.clone()
    val lore = meta.lore ?: mutableListOf()
    val key = NamespacedKey(JavaPlugin.getPlugin(Maple::class.java), "custom-property")
    val value = PersistentDataType.INTEGER

    val acurrentValue = meta.persistentDataContainer.get(key, value) ?: 0

    val successarr = arrayOf(100, 90, 80, 70, 60, 50, 40, 30, 20, 10)
    val failarr = arrayOf(0, 10, 20, 30, 30, 30, 30, 30, 40, 10)
    val downarr = arrayOf(0, 0, 0, 0, 10, 20, 20, 30, 30, 50)
    val breakarr = arrayOf(0, 0, 0, 0, 0, 0, 10, 10, 10, 30)

    if(lore.size == 0){
        lore.add("${ChatColor.GRAY}이 아이템은 특별한 힘이 담겨있다.")
        lore.add("${ChatColor.GOLD}☆☆☆☆☆☆☆☆☆☆")
        lore.add("${ChatColor.GREEN}성공확률: 100%")
        lore.add("${ChatColor.BLUE}실패확률: 0%")
        lore.add("${ChatColor.RED}하락확률: 0%")
        lore.add("${ChatColor.DARK_RED}파괴확률: 0%")
        meta.persistentDataContainer.set(key, value, 0)
    } else {
        if(rand(acurrentValue) == 1){//성공
            val currentValue = meta.persistentDataContainer.get(key, value) ?: 0
            val increasedValue = currentValue + 1
            meta.persistentDataContainer.set(key, value, increasedValue)
            val starCount = minOf(increasedValue, 10)
            lore[1] = "${ChatColor.GOLD}${"★".repeat(starCount)}${ChatColor.GRAY}${"☆".repeat(10 - starCount)}"
            lore[2] = "${ChatColor.GREEN}성공확률: ${successarr[currentValue]}%"
            lore[3] = "${ChatColor.BLUE}실패확률: ${failarr[currentValue]}%"
            lore[4] = "${ChatColor.RED}하락확률: ${downarr[currentValue]}%"
            lore[5] = "${ChatColor.DARK_RED}파괴확률: ${breakarr[currentValue]}%"
        }else if(rand(acurrentValue) == 2) {
            val currentValue = meta.persistentDataContainer.get(key, value) ?: 0
            lore[2] = "${ChatColor.GREEN}성공확률: ${successarr[currentValue]}%"
            lore[3] = "${ChatColor.BLUE}실패확률: ${failarr[currentValue]}%"
            lore[4] = "${ChatColor.RED}하락확률: ${downarr[currentValue]}%"
            lore[5] = "${ChatColor.DARK_RED}파괴확률: ${breakarr[currentValue]}%"
            player.sendMessage("실패!")
        } else if(rand(acurrentValue) == 3){
            val currentValue = meta.persistentDataContainer.get(key, value) ?: 0
            val decreasedValue = currentValue - 1 // 값을 -1로 감소시킴
            meta.persistentDataContainer.set(key, value, decreasedValue)

            val starCount = maxOf(decreasedValue, 0) // 최소값 0으로 설정
            lore[1] = "${ChatColor.GOLD}${"★".repeat(starCount)}${ChatColor.GRAY}${"☆".repeat(10 - starCount)}"
            lore[2] = "${ChatColor.GREEN}성공확률: ${successarr[currentValue]}%"
            lore[3] = "${ChatColor.BLUE}실패확률: ${failarr[currentValue]}%"
            lore[4] = "${ChatColor.RED}하락확률: ${downarr[currentValue]}%"
            lore[5] = "${ChatColor.DARK_RED}파괴확률: ${breakarr[currentValue]}%"
            player.sendMessage("아이쿠! 손이 미끄러졌네요!")
        } else if(rand(acurrentValue) == 4) {
            player.closeInventory()
            player.inventory.remove(item)
            player.sendMessage("수준ㅋㅋㅋㅋ")
        }
    }
    item.itemMeta = meta
    val reinforcedItem = item.clone() // 아이템 복제
    val reinforcedMeta = reinforcedItem.itemMeta
    reinforcedMeta.lore = lore
    reinforcedItem.itemMeta = reinforcedMeta
    return reinforcedItem
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