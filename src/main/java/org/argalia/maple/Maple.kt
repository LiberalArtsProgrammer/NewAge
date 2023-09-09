package org.argalia.maple

import io.github.monun.invfx.InvFX
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import starforce
import java.util.*

class Maple : JavaPlugin(), Listener {
    companion object {
        private lateinit var pluginInstance: Maple // 플러그인 인스턴스 저장을 위한 변수

        fun getPlugin(): Maple {
            return pluginInstance
        }

        val moneyKey: NamespacedKey by lazy {
            NamespacedKey(getPlugin(), "player_money")
        }
    }

    fun generateStarForceInventory(player: Player, targetitem:ItemStack) {
        val inventory = player.inventory
        var reinforcedItem = targetitem.clone() // 초기 아이템 복제

        val invFrame = InvFX.frame(1, Component.text("강화")) {
            onOpen { openEvent ->
                openEvent.player.sendMessage("강화를 시작합니다.")
            }
            slot (4, 0) {
                item = reinforcedItem // 초기 아이템을 표시
                onClick { clickEvent ->
                    reinforcedItem = starforce(reinforcedItem, player) // 아이템 강화
                    item = reinforcedItem
                }
            }
            onClose { closeEvent ->
                val itemMeta = reinforcedItem.itemMeta
                val lore = itemMeta.lore
                if (lore != null && lore.size >= 6) {
                    lore.removeAt(5) // 6번째 줄 삭제
                    lore.removeAt(4) // 5번째 줄 삭제
                    lore.removeAt(3) // 4번째 줄 삭제
                    lore.removeAt(2) // 3번째 줄 삭제
                    itemMeta.lore = lore
                    reinforcedItem.itemMeta = itemMeta
                }
                inventory.setItemInMainHand(reinforcedItem)
            }
        }
        player.openFrame(invFrame)

        inventory.setItemInMainHand(null)
    }

    fun generateShopInventory(player: Player) {
        val inventory = player.inventory
        val invFrame = InvFX.frame(3, Component.text("상점")) {
            onOpen { openEvent ->
                openEvent.player.sendMessage("상점을 시작합니다.")
            }
            slot (4, 1) {
                item = ItemStack(Material.DIAMOND)
                onClick { clickEvent ->
                    val diamondsInInventory = inventory.all(Material.DIAMOND)
                    if (diamondsInInventory.isNotEmpty()) {
                        // 플레이어 인벤토리에서 다이아몬드 하나를 제거
                        val slotEntry = diamondsInInventory.entries.first()
                        val slot = slotEntry.key
                        val diamondAmount = slotEntry.value.amount
                        if (diamondAmount > 1) {
                            slotEntry.value.amount = diamondAmount - 1
                        } else {
                            inventory.setItem(slot, null)
                        }

                        // 플레이어의 Money를 증가시킴 (여기서는 1000으로 설정)
                        increasePlayerMoney(player, 1000)

                        player.sendMessage("다이아몬드를 판매하였습니다.")
                    } else {
                        player.sendMessage("다이아몬드가 부족합니다.")
                    }
                }
            }
        }
        player.openFrame(invFrame)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        pluginInstance = this
    }

    override fun onDisable() {

    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender is Player){
            if (command.name.equals("starforce", ignoreCase = true)) {
                val heldItem = sender.inventory.itemInMainHand
                if (!heldItem.type.isAir) {
                    generateStarForceInventory(sender, heldItem.clone())
                } else {
                    sender.sendMessage("아이템을 들고 명령어를 실행해주세염...")
                }
                return true
            } else if(command.name.equals("showMoney", ignoreCase = true)) {
                showPlayerMoney(sender)
            } else if(command.name.equals("shop", ignoreCase = true)){
                generateShopInventory(sender)
            }
        }
        return false
    }

    @EventHandler
    fun changeArrowDamage(e: EntityDamageByEntityEvent) {
        if (e.cause == DamageCause.PROJECTILE && e.damager is Arrow) {
            val arrow = e.damager as Arrow
            if (arrow.shooter is Player) {
                val player = arrow.shooter as Player
                val item = player.inventory.itemInMainHand
                val meta = item.itemMeta
                val key = NamespacedKey(getPlugin(Maple::class.java), "starforce")
                val value = PersistentDataType.INTEGER
                val CurrentValue = meta.persistentDataContainer.get(key, value) ?: 0
                e.damage = e.damage + CurrentValue*CurrentValue
            }
        }
    }

    /**@EventHandler//칭호 시스템
    fun onChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        val message = e.message
        e.format = "${ChatColor.GOLD}[창조] ${ChatColor.GREEN}${player.displayName}${ChatColor.RESET}: $message"
    }**/

    @EventHandler//사망시 패널티
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity as? Player ?: return

        val inventory = player.inventory

        // 랜덤한 슬롯 선택
        val random = Random()
        val slotIndex = random.nextInt(inventory.size)
        player.sendMessage("인벤토리의 아이템을 희생하여 죽음을 초월합니다.")
        // 선택된 슬롯의 아이템 삭제
        inventory.setItem(slotIndex, ItemStack(Material.AIR))

        // 인벤토리 업데이트 (클라이언트에 변경 사항 표시)
        player.updateInventory()
    }


    private fun isArmorItem(item: ItemStack): Boolean {
        return item.type.name.endsWith("_HELMET") ||
                item.type.name.endsWith("_CHESTPLATE") ||
                item.type.name.endsWith("_LEGGINGS") ||
                item.type.name.endsWith("_BOOTS")
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        giveHealthArmor(player)
    }

    private fun giveHealthArmor(player: Player) {
        val healthArmor = ItemStack(Material.DIAMOND_CHESTPLATE)
        val meta = healthArmor.itemMeta

        // 갑옷의 이름 설정
        //meta.setDisplayName("생명의 갑옷")

        // Attribute를 사용하여 추가 체력 설정
        val attributeModifier = AttributeModifier(UUID.randomUUID(), "health_armor", 100.0, AttributeModifier.Operation.ADD_NUMBER,  EquipmentSlot.CHEST)
        meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, attributeModifier)

        healthArmor.itemMeta = meta

        // 플레이어에게 갑옷 주기
        player.inventory.addItem(healthArmor)
    }
}
