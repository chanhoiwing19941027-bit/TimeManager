package com.hkaiSolveAcademy.vibeCoding.wing.data.database

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.ui.graphics.vector.ImageVector

data class TemplateTask(
    val title: String,
    val category: Category,
    val durationMinutes: Int,
    val scheduledTime: String // Format: HH:mm
)

data class TimetableTemplate(
    val id: String,
    val name: String,
    val description: String,
    val quote: String, // 模式專屬金句 (Value-add)
    val teachingSteps: List<String>, // 詳細操作步驟 (Methodology Coach)
    val icon: ImageVector,
    val tasks: List<TemplateTask>
)

object TemplateRegistry {
    val templates = listOf(
        TimetableTemplate(
            id = "elon_musk",
            name = "伊隆·馬斯克模式",
            description = "極致的時間方塊管理法",
            quote = "「如果你給自己三十天完成一項任務，你就會花三十天。如果你給自己五分鐘，你就會在五分鐘內完成。」",
            teachingSteps = listOf(
                "第一步：將所有大任務拆解成不超過 30 分鐘的子任務。",
                "第二步：使用『時間方塊』，將每分每秒都填入行事曆，不留空白。",
                "第三步：批處理 (Batching) 郵件與訊息，每天只在固定時間回覆。",
                "第四步：站著開會。縮短不必要的溝通時間。"
            ),
            icon = Icons.Default.RocketLaunch,
            tasks = listOf(
                TemplateTask("郵件批處理與關鍵指令", Category.WORK, 15, "08:00"),
                TemplateTask("工程技術設計深度審查", Category.WORK, 45, "08:15"),
                TemplateTask("SpaceX / Tesla 生產線對接", Category.WORK, 30, "09:00"),
                TemplateTask("決策會議：快速解決方案", Category.WORK, 20, "09:30"),
                TemplateTask("健康午餐：快速營養補充", Category.HEALTH, 15, "12:00")
            )
        ),
        TimetableTemplate(
            id = "chill_deep_work",
            name = "愜意深度工作模式",
            description = "追求高品質產出與大腦恢復",
            quote = "「在高強度的專注之後，大腦需要徹底的放空才能產生新的創意。」",
            teachingSteps = listOf(
                "第一步：找出今天最重要的兩件事 (The Big Two)。",
                "第二步：進入『深度工作區塊』，關閉手機，斷開一切社交軟體。",
                "第三步：長午休。透過散步或輕量運動讓大腦進入『發散模式』。",
                "第四步：下午進行低強度的瑣事處理，保持心情愉快。"
            ),
            icon = Icons.Default.Coffee,
            tasks = listOf(
                TemplateTask("深度工作：核心模組開發", Category.WORK, 150, "09:00"),
                TemplateTask("大腦恢復：公園散步與放空", Category.HEALTH, 90, "11:30"),
                TemplateTask("深度工作：疑難雜症攻克", Category.WORK, 120, "14:00"),
                TemplateTask("日常維護與郵件回覆", Category.PERSONAL, 30, "16:30")
            )
        ),
        TimetableTemplate(
            id = "tight_crunch_mode",
            name = "極限衝刺模式 (Tight)",
            description = "Deadline 救星，極高頻專注循環",
            quote = "「專注力就像肌肉，越是極限使用，爆發力就越強。」",
            teachingSteps = listOf(
                "第一步：使用 50/10 循環。專注 50 分鐘，休息 10 分鐘。",
                "第二步：嚴禁多工處理 (Multi-tasking)，每次只專注一個目標。",
                "第三步：站立式工作或每小時起身活動，保持血液循環。",
                "第四步：完成一組後給予自己小獎勵（如：一杯咖啡）。"
            ),
            icon = Icons.Default.Bolt,
            tasks = listOf(
                TemplateTask("第一階段：高強度開發", Category.WORK, 50, "09:00"),
                TemplateTask("第一階段：伸展與補水", Category.HEALTH, 10, "09:50"),
                TemplateTask("第二階段：高強度開發", Category.WORK, 50, "10:00"),
                TemplateTask("第二階段：快速放鬆", Category.HEALTH, 10, "10:50"),
                TemplateTask("第三階段：最終測試與整合", Category.WORK, 50, "11:00")
            )
        )
    )
}
