package org.argalia.maple

import org.bukkit.plugin.java.JavaPlugin
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import starforce
class Maple : JavaPlugin(), Listener {
    fun generateInventory(player: Player, targetitem:ItemStack) {
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
                    item = reinforcedItem // 강화된 아이템으로 변경
                }
            }
            onClose { closeEvent ->
                inventory.addItem(reinforcedItem)
            }
        }
        player.openFrame(invFrame)

        inventory.setItemInMainHand(null)
    }
    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {

    }

    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val entity = event.entity

        if (entity is Arrow && entity.shooter is Player) {
            val arrow = entity
            val hitLocation = arrow.location

            // 화살이 닿은 곳에 폭발 생성
            hitLocation.world.createExplosion(hitLocation, 5.0f)
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("starforce", ignoreCase = true)) {
            if (sender is Player) {
                val heldItem = sender.inventory.itemInMainHand
                if (!heldItem.type.isAir) {
                    generateInventory(sender, heldItem.clone())
                } else {
                    sender.sendMessage("아이템을 들고 명령어를 실행해주세염...")
                }
            }
            return true
        }
        return false
    }
}
