package org.argalia.maple

import generateStarForceInventory
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class Maple : JavaPlugin(), Listener {
    companion object {
        private lateinit var pluginInstance: Maple // 플러그인 인스턴스 저장을 위한 변수

        fun getPlugin(): Maple {
            return pluginInstance
        }

        val moneyKey: NamespacedKey by lazy {
            NamespacedKey(getPlugin(), "Money")
        }

        val starKey: NamespacedKey by lazy {
            NamespacedKey(getPlugin(), "Star")
        }
    }
    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        pluginInstance = this

        this.kommand {
            register("starforce") {
                executes {
                    val heldItem = player.inventory.itemInMainHand
                    generateStarForceInventory(player, heldItem)
                }
            }
            register("money") {
                executes {
                    giveMoney(player, 1000000)
                }
            }
            register("stargogo") {
                executes {
                    player.sendMessage(text(getStar(player.inventory.itemInMainHand)))
                }
            }
            register("starga") {
                executes {
                    giveStar(player.inventory.itemInMainHand, 1)
                }
            }
        }
    }

    override fun onDisable() {

    }

    /**@EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        if (event.entity is Arrow) {
            val arrow = event.entity as Arrow
            if (arrow.shooter is Player) {
                arrow.world.createExplosion(arrow.location, 5.0f, true, true)
                arrow.remove()
            }
        }
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


    fun setMp(player: Player, value: Int) { // 플레이어에게 마나를 저장하는 함수
        player.persistentDataContainer.set(NamespacedKey(this, "Mp"), PersistentDataType.INTEGER, value)
    }

    fun getMp(player: Player): Int { // 플레이어로부터 마나를 가져오는 함수
        return player.persistentDataContainer.get(NamespacedKey(this, "Mp"), PersistentDataType.INTEGER) ?: 0
    }

    fun giveMp(player: Player, value: Int) { // 플레이어에게 마나를 변경하는 함수
        player.persistentDataContainer.set(NamespacedKey(this, "Mp"), PersistentDataType.INTEGER, getMp(player) + value)
    }
}
