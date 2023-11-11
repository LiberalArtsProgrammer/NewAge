import io.github.monun.invfx.InvFX
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.argalia.maple.*
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

fun generateStarForceInventory(player: Player, targetitem:ItemStack) {
    val inventory = player.inventory
    var reinforcedItem = targetitem.clone() // 초기 아이템 복제

    val invFrame = InvFX.frame(1, Component.text("강화")) {
        onOpen { openEvent ->

        }
        slot (4, 0) {
            val meta: ItemMeta = reinforcedItem.itemMeta
            val Lore = meta.lore ?: mutableListOf()
            if(Lore.size == 0) {
                Lore.add("${ChatColor.GRAY}☆☆☆☆☆☆☆☆☆☆")
                Lore.add("${ChatColor.GREEN}성공확률: 100%")
                Lore.add("${ChatColor.BLUE}실패확률: 0%")
                Lore.add("${ChatColor.RED}하락확률: 0%")
                Lore.add("${ChatColor.DARK_RED}파괴확률: 0%")
            }
            // 업데이트된 lore를 메타데이터에 설정합니다.
            meta.lore = Lore
            reinforcedItem.itemMeta = meta

            item = reinforcedItem // 초기 아이템을 표시
            onClick { clickEvent ->
                reinforcedItem = starforce(reinforcedItem, player) // 아이템 강화
                item = reinforcedItem
            }
        }
        onClose { closeEvent ->
            inventory.setItemInMainHand(reinforcedItem)
        }
    }
    player.openFrame(invFrame)

    inventory.setItemInMainHand(null)
}

fun starforce(item: ItemStack, player: Player): ItemStack {
    if(getMoney(player)>10000) {
        giveMoney(player, -10000)
        val meta = item.itemMeta ?: return item.clone()
        val lore = meta.lore ?: mutableListOf()
        val currentValue = getStar(item)
        player.sendMessage(text("전" + getStar(item)))
        val successarr = arrayOf(100, 90, 80, 70, 60, 50, 40, 30, 20, 10)
        val failarr = arrayOf(0, 10, 20, 30, 30, 30, 30, 30, 40, 10)
        val downarr = arrayOf(0, 0, 0, 0, 10, 20, 20, 30, 30, 50)
        val breakarr = arrayOf(0, 0, 0, 0, 0, 0, 10, 10, 10, 30)

        when (rand(currentValue)) {
            1 -> {
                giveStar(item, 1)
                val starCount = minOf(getStar(item), 10)
                lore[0] = "${ChatColor.GOLD}${"★".repeat(starCount)}${ChatColor.GRAY}${"☆".repeat(10 - starCount)}"
            }
            2 -> {
                player.sendMessage("실패!")
            }
            3 -> {
                player.sendMessage("아이쿠! 손이 미끄러졌네!")
                // 실패 시 다운그레이드 로직
                if (currentValue > 0) {
                    giveStar(item, -1)
                    val starCount = maxOf(getStar(item), 0)
                    lore[0] = "${ChatColor.GOLD}${"★".repeat(starCount)}${ChatColor.GRAY}${"☆".repeat(10 - starCount)}"
                }
            }
            4 -> {
                player.closeInventory()
                player.sendMessage("수준ㅋㅋㅋ")
                player.inventory.setItemInMainHand(null)
            }
        }
        //
        val laterCurrentValue = getStar(item)
        lore[1] = "${ChatColor.GREEN}성공확률: ${successarr[laterCurrentValue]}%"
        lore[2] = "${ChatColor.BLUE}실패확률: ${failarr[laterCurrentValue]}%"
        lore[3] = "${ChatColor.RED}하락확률: ${downarr[laterCurrentValue]}%"
        lore[4] = "${ChatColor.DARK_RED}파괴확률: ${breakarr[laterCurrentValue]}%"
        //

        item.itemMeta = meta
        val reinforcedItem = item.clone()
        val reinforcedMeta = reinforcedItem.itemMeta
        reinforcedMeta.lore = lore
        reinforcedItem.itemMeta = reinforcedMeta
        player.sendMessage(text("후" + getStar(item)))
        return reinforcedItem
    } else {
        player.closeInventory()
        player.sendMessage("돈이 없어요...")
        return item
    }
}

fun rand(i : Int): Int {
    val random = Random()
    val successarr = arrayOf(100, 90, 80, 70, 60, 50, 40, 30, 20, 10)
    val failarr = arrayOf(0, 10, 20, 30, 30, 30, 30, 30, 40, 10)
    val downarr = arrayOf(0, 0, 0, 0, 10, 20, 20, 30, 30, 50)
    val breakarr = arrayOf(0, 0, 0, 0, 0, 0, 10, 10, 10, 30)


    val randomValue = random.nextInt(0,101)
    val successProbability = successarr[i]
    val failProbability = failarr[i]
    val downProbability = downarr[i]
    val breakProbability = breakarr[i]
    when {
        randomValue < successProbability -> return 1
        randomValue < successProbability + failProbability -> return 2
        randomValue < successProbability + failProbability + downProbability -> return 3
        else -> return 4
    }
}