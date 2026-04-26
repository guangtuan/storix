package com.storix.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.HomeWork
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.storix.app.data.local.Asset
import com.storix.app.data.local.AssetCategory
import com.storix.app.data.local.Member
import com.storix.app.ui.theme.StorixGreenLight
import com.storix.app.ui.theme.ThemePreset
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.launch

private const val HomeRoute = "home"
private const val DetailRoute = "detail"
private const val EditRoute = "edit"
private const val FeaturedTagAlpha = 0.12f
private val SummaryMetricMinWidth = 104.dp
private val SummaryMetricMaxWidth = 160.dp

private enum class MainTab(
    val label: String,
    val icon: ImageVector
) {
    HOME(label = "首页", icon = Icons.Rounded.Home),
    TIMELINE(label = "时间轴", icon = Icons.Rounded.Schedule),
    SETTINGS(label = "设置", icon = Icons.Rounded.Settings)
}

private data class SummaryMetricItem(val title: String, val value: String)
private data class TimelineYearGroup(val year: Int, val assets: List<Asset>)

private fun detailRoute(assetId: Long): String = "$DetailRoute/$assetId"
private fun editRoute(assetId: Long): String = "$EditRoute/$assetId"

@Composable
fun StorixApp(
    viewModel: MainViewModel,
    selectedThemePreset: ThemePreset,
    onThemePresetChange: (ThemePreset) -> Unit
) {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        NavHost(navController = navController, startDestination = HomeRoute) {
            composable(HomeRoute) {
                HomeScreen(
                    viewModel = viewModel,
                    onAddAsset = { navController.navigate(EditRoute) },
                    onOpenAsset = { navController.navigate(detailRoute(it)) },
                    selectedThemePreset = selectedThemePreset,
                    onThemePresetChange = onThemePresetChange
                )
            }
            composable(
                route = "$DetailRoute/{assetId}",
                arguments = listOf(navArgument("assetId") { type = NavType.LongType })
            ) { entry ->
                DetailScreen(
                    assetId = entry.arguments?.getLong("assetId") ?: 0,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(editRoute(it)) }
                )
            }
            composable(EditRoute) {
                AssetEditorScreen(
                    assetId = null,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            composable(
                route = "$EditRoute/{assetId}",
                arguments = listOf(navArgument("assetId") { type = NavType.LongType })
            ) { entry ->
                AssetEditorScreen(
                    assetId = entry.arguments?.getLong("assetId"),
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    viewModel: MainViewModel,
    onAddAsset: () -> Unit,
    onOpenAsset: (Long) -> Unit,
    selectedThemePreset: ThemePreset,
    onThemePresetChange: (ThemePreset) -> Unit
) {
    val uiState by viewModel.homeUiState.collectAsState()
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

    Scaffold(
        topBar = {
            TelegramTopBar(title = "Storix", subtitle = "我的收藏")
        },
        floatingActionButton = {
            if (selectedTab == MainTab.HOME) {
                FloatingActionButton(
                    onClick = onAddAsset,
                    modifier = Modifier
                        .padding(end = 10.dp, bottom = 10.dp)
                        .size(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            }
        },
        bottomBar = {
            MainBottomBar(selectedTab = selectedTab, onSelected = { selectedTab = it })
        }
    ) { paddingValues ->
        when (selectedTab) {
            MainTab.HOME -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        SummaryCard(uiState = uiState)
                    }

                    if (uiState.assets.isEmpty()) {
                        item {
                            EmptyStateCard(onAddAsset = onAddAsset)
                        }
                    } else {
                        items(uiState.assets.chunked(2), key = { row -> row.first().id }) { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                row.forEach { asset ->
                                    AssetGridCard(
                                        asset = asset,
                                        isFeatured = uiState.longestCompanionAsset?.id == asset.id && !asset.isRetired,
                                        onClick = { onOpenAsset(asset.id) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (row.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
            MainTab.TIMELINE -> {
                TimelineContent(
                    assets = uiState.assets,
                    members = uiState.members,
                    defaultMember = uiState.defaultMember,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onOpenAsset = onOpenAsset
                )
            }
            MainTab.SETTINGS -> {
                ThemeSettingsContent(
                    viewModel = viewModel,
                    members = uiState.members,
                    selectedThemePreset = selectedThemePreset,
                    onThemePresetChange = onThemePresetChange,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun MainBottomBar(selectedTab: MainTab, onSelected: (MainTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MainTab.values().forEach { tab ->
            val selected = selectedTab == tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable { onSelected(tab) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ThemeSettingsContent(
    viewModel: MainViewModel,
    members: List<Member>,
    selectedThemePreset: ThemePreset,
    onThemePresetChange: (ThemePreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeRows = remember { ThemePreset.entries.toList().chunked(2) }
    val scope = rememberCoroutineScope()
    var isTransferringData by rememberSaveable { mutableStateOf(false) }
    var transferResultMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showImportConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var memberActionMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showMemberDialog by rememberSaveable { mutableStateOf(false) }
    var editingMemberId by rememberSaveable { mutableStateOf<Long?>(null) }
    var memberNameInput by rememberSaveable { mutableStateOf("") }
    var memberAvatarInput by rememberSaveable { mutableStateOf("") }
    var memberErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var savingMember by rememberSaveable { mutableStateOf(false) }

    val memberAvatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            memberAvatarInput = uri.toString()
            memberErrorMessage = null
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            isTransferringData = true
            val result = viewModel.exportAssetsToUri(uri)
            transferResultMessage = result.message
            isTransferringData = false
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            isTransferringData = true
            val result = viewModel.importAssetsFromUri(uri)
            transferResultMessage = result.message
            isTransferringData = false
        }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 12.dp, top = 10.dp, end = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "主题色",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "选择你喜欢的界面主题色，立即生效。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    themeRows.forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { preset ->
                                FilterChip(
                                    selected = selectedThemePreset == preset,
                                    onClick = { onThemePresetChange(preset) },
                                    modifier = Modifier.weight(1f),
                                    label = { Text(preset.displayName) },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(preset.swatch)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.People,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "成员管理",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                editingMemberId = null
                                memberNameInput = ""
                                memberAvatarInput = ""
                                memberErrorMessage = null
                                showMemberDialog = true
                            }
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("新增")
                        }
                    }

                    Text(
                        text = "默认成员用于承接未指定成员的历史物品。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (members.isEmpty()) {
                        Text(
                            text = "暂无成员，系统会自动创建默认成员。",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        members.forEach { member ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MemberAvatar(
                                        member = member,
                                        size = 34.dp
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = member.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (member.isDefault) {
                                                AssetMetaTag(
                                                    text = "默认",
                                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                                    contentColor = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                        Text(
                                            text = if (member.isDefault) "用于承接未绑定成员的物品" else "可绑定资产归属",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (!member.isDefault) {
                                    TextButton(
                                        onClick = {
                                            scope.launch {
                                                viewModel.setDefaultMember(member.id)
                                                memberActionMessage = "已设默认成员：${member.name}"
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Rounded.Star, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("设默认")
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        editingMemberId = member.id
                                        memberNameInput = member.name
                                        memberAvatarInput = member.avatarUrl.orEmpty()
                                        memberErrorMessage = null
                                        showMemberDialog = true
                                    }
                                ) {
                                    Icon(Icons.Rounded.Edit, contentDescription = "编辑成员")
                                }
                                if (!member.isDefault) {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                val deleted = viewModel.deleteMember(member)
                                                memberActionMessage = if (deleted) {
                                                    "已删除成员：${member.name}"
                                                } else {
                                                    "默认成员不能删除"
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "删除成员")
                                    }
                                }
                            }
                        }
                    }

                    memberActionMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PhotoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "数据迁移",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "导出后可在新安装版本中导入，适合从调试包迁移到正式包。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                transferResultMessage = null
                                val fileName = "storix-backup-${System.currentTimeMillis()}.json"
                                exportLauncher.launch(fileName)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isTransferringData
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FileDownload,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("导出")
                        }

                        Button(
                            onClick = {
                                showImportConfirmDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isTransferringData
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FileUpload,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("导入")
                        }
                    }

                    if (isTransferringData) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Text(
                                text = "处理中...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    val message = transferResultMessage
                    if (message != null) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showImportConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showImportConfirmDialog = false },
            title = { Text("确认导入") },
            text = { Text("导入会覆盖当前资产数据，建议先执行一次导出备份。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportConfirmDialog = false
                        transferResultMessage = null
                        importLauncher.launch(arrayOf("application/json", "text/plain"))
                    }
                ) {
                    Text("继续")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showMemberDialog) {
        val editingMember = members.firstOrNull { it.id == editingMemberId }
        AlertDialog(
            onDismissRequest = {
                if (!savingMember) {
                    showMemberDialog = false
                }
            },
            title = { Text(if (editingMember == null) "新增成员" else "编辑成员") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = memberNameInput,
                        onValueChange = {
                            memberNameInput = it
                            memberErrorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("成员名字") },
                        singleLine = true
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MemberAvatar(
                            member = null,
                            avatarUrlOverride = memberAvatarInput.ifBlank { null },
                            displayNameOverride = memberNameInput.trim().ifBlank { "成员" },
                            size = 42.dp
                        )
                        OutlinedButton(
                            onClick = {
                                memberAvatarPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        ) {
                            Icon(Icons.Rounded.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("从相册选择")
                        }
                    }
                    memberErrorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val normalizedName = memberNameInput.trim()
                        if (normalizedName.isBlank()) {
                            memberErrorMessage = "成员名字不能为空"
                            return@TextButton
                        }
                        scope.launch {
                            savingMember = true
                            viewModel.saveMember(
                                existingMember = editingMember,
                                name = normalizedName,
                                avatarUrl = memberAvatarInput.ifBlank { null }
                            )
                            memberActionMessage = if (editingMember == null) {
                                "已新增成员：$normalizedName"
                            } else {
                                "已更新成员：$normalizedName"
                            }
                            savingMember = false
                            showMemberDialog = false
                        }
                    },
                    enabled = !savingMember
                ) {
                    Text(if (savingMember) "保存中..." else "保存")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showMemberDialog = false },
                    enabled = !savingMember
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun TimelineContent(
    assets: List<Asset>,
    members: List<Member>,
    defaultMember: Member?,
    modifier: Modifier = Modifier,
    onOpenAsset: (Long) -> Unit
) {
    val groupedTimeline = remember(assets) {
        val zoneId = ZoneId.systemDefault()
        assets
            .sortedByDescending { it.purchaseDate }
            .groupBy {
                Instant.ofEpochMilli(it.purchaseDate)
                    .atZone(zoneId)
                    .year
            }
            .toSortedMap(compareByDescending { it })
            .map { (year, yearlyAssets) ->
                TimelineYearGroup(
                    year = year,
                    assets = yearlyAssets.sortedByDescending { it.purchaseDate }
                )
            }
    }

    if (groupedTimeline.isEmpty()) {
        Box(modifier = modifier.padding(12.dp), contentAlignment = Alignment.Center) {
            Text("还没有可展示的时间轴", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 12.dp, top = 10.dp, end = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val memberMap = members.associateBy { it.id }
        groupedTimeline.forEach { group ->
            item(key = "timeline-year-${group.year}") {
                TimelineYearHeader(year = group.year, count = group.assets.size)
            }
            itemsIndexed(group.assets, key = { _, asset -> asset.id }) { index, asset ->
                TimelineItem(
                    asset = asset,
                    member = memberMap[asset.memberId] ?: defaultMember,
                    showConnector = index < group.assets.lastIndex,
                    onClick = { onOpenAsset(asset.id) }
                )
            }
        }
    }
}

@Composable
private fun TimelineYearHeader(year: Int, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${year}年",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "$count 条",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TimelineItem(asset: Asset, member: Member?, showConnector: Boolean, onClick: () -> Unit) {
    val accent = categoryAccent(asset.category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 14.dp)
                .width(18.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accent.contentColor)
            )
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        OutlinedCard(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Formatters.formatMonthDay(asset.purchaseDate),
                        style = MaterialTheme.typography.titleSmall,
                        color = accent.contentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MemberAvatar(
                            member = member,
                            size = 28.dp
                        )
                        Text(
                            text = member?.name ?: "默认成员",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "购入 ${Formatters.formatCurrency(asset.purchaseValue, asset.currency)} · 已陪伴 ${Formatters.formatHoldingPeriod(asset.purchaseDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MemberAvatar(
    member: Member?,
    size: androidx.compose.ui.unit.Dp,
    avatarUrlOverride: String? = null,
    displayNameOverride: String? = null
) {
    val avatarUrl = avatarUrlOverride ?: member?.avatarUrl
    val displayName = displayNameOverride ?: member?.name ?: "成员"
    val placeholder = displayName.trim().firstOrNull()?.toString() ?: "?"

    if (!avatarUrl.isNullOrBlank()) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = displayName,
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        return
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (placeholder == "?") {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SummaryCard(uiState: HomeUiState) {
    val metrics = buildSummaryMetrics(uiState)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "总购入金额",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
            )
            Text(
                text = Formatters.formatCurrency(uiState.totalOriginalCost, "CNY"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            LazyRow(
                modifier = Modifier.semantics { contentDescription = "摘要统计列表" },
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(metrics) { metric ->
                    SummaryMetric(
                        title = metric.title,
                        value = metric.value,
                        modifier = Modifier.widthIn(min = SummaryMetricMinWidth, max = SummaryMetricMaxWidth)
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryMetric(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.68f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyStateCard(onAddAsset: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text("收藏夹还是空的", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = "添加一件物品开始记录，记下购入金额、买入日期和陪伴时光。",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onAddAsset, shape = RoundedCornerShape(10.dp)) {
                Text("现在添加")
            }
        }
    }
}

@Composable
private fun AssetGridCard(
    asset: Asset,
    isFeatured: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = categoryAccent(asset.category)

    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        border = if (isFeatured) BorderStroke(1.dp, accent.contentColor.copy(alpha = 0.22f)) else null,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon(asset.category),
                        contentDescription = null,
                        tint = accent.contentColor
                    )
                }
                AssetMetaTag(
                    text = if (asset.isRetired) "已归档" else "陪伴中",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "已陪伴 ${Formatters.formatHoldingPeriod(asset.purchaseDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accent.contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "购入 ${Formatters.formatCurrency(asset.purchaseValue, asset.currency)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                AssetMetaTag(
                    text = asset.category.displayName,
                    containerColor = accent.containerColor,
                    contentColor = accent.contentColor
                )
                if (isFeatured) {
                    AssetMetaTag(
                        text = "陪伴最久",
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = FeaturedTagAlpha),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetMetaTag(text: String, containerColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(containerColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    assetId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val assetFlow = remember(assetId) { viewModel.observeAsset(assetId) }
    val asset by assetFlow.collectAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val targetAsset = asset ?: return@rememberLauncherForActivityResult
            scope.launch {
                viewModel.saveAsset(
                    existingAsset = targetAsset,
                    name = targetAsset.name,
                    category = targetAsset.category,
                    description = targetAsset.description,
                    purchaseValue = targetAsset.purchaseValue,
                    isRetired = targetAsset.isRetired,
                    purchaseDate = targetAsset.purchaseDate,
                    currency = targetAsset.currency,
                    imageUrl = uri.toString(),
                    memberId = targetAsset.memberId,
                    location = targetAsset.location,
                    notes = targetAsset.notes
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TelegramTopBar(
                title = asset?.name ?: "物品详情",
                subtitle = asset?.category?.displayName ?: "",
                onBack = onBack,
                actions = {
                    asset?.let {
                        IconButton(onClick = { onEdit(it.id) }) {
                            Icon(Icons.Rounded.Edit, contentDescription = "编辑")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "删除")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val currentAsset = asset
        if (currentAsset == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("未找到这条记录")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AssetImageCard(
                        imageUrl = currentAsset.imageUrl,
                        category = currentAsset.category,
                        onClick = {
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        placeholderHint = "点击这里上传图片"
                    )
                }
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = currentAsset.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (currentAsset.description.isNotBlank()) {
                                Text(
                                    text = currentAsset.description,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AssistChip(
                                onClick = {},
                                label = { Text(currentAsset.category.displayName) },
                                leadingIcon = {
                                    Icon(categoryIcon(currentAsset.category), contentDescription = null)
                                }
                            )
                        }
                    }
                }
                item {
                    MetricSection(currentAsset)
                }
                if (currentAsset.location.isNotBlank() || currentAsset.notes.isNotBlank()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("补充信息", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                if (currentAsset.location.isNotBlank()) {
                                    DetailLine(
                                        icon = Icons.Rounded.LocationOn,
                                        label = "位置",
                                        value = currentAsset.location
                                    )
                                }
                                if (currentAsset.notes.isNotBlank()) {
                                    DetailLine(
                                        icon = Icons.AutoMirrored.Rounded.Notes,
                                        label = "备注",
                                        value = currentAsset.notes
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && asset != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除收藏") },
            text = { Text("删除后这条记录会从本地列表中移除。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = asset ?: return@TextButton
                        scope.launch {
                            viewModel.deleteAsset(value)
                            showDeleteDialog = false
                            onBack()
                        }
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun MetricSection(asset: Asset) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("记录信息", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            DetailLine(Icons.Rounded.Info, "购入金额", Formatters.formatCurrency(asset.purchaseValue, asset.currency))
            DetailLine(Icons.Rounded.Info, "当前状态", if (asset.isRetired) "已归档" else "正在陪伴")
            DetailLine(Icons.Rounded.CalendarMonth, "买入日期", Formatters.formatDate(asset.purchaseDate))
            DetailLine(Icons.Rounded.CalendarMonth, "陪伴时长", Formatters.formatHoldingPeriod(asset.purchaseDate))
        }
    }
}

@Composable
private fun DetailLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color? = null
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, color = valueColor ?: MaterialTheme.colorScheme.onSurface)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssetEditorScreen(
    assetId: Long?,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val assetFlow = remember(assetId) { assetId?.let(viewModel::observeAsset) }
    val existingAsset = if (assetFlow != null) {
        assetFlow.collectAsState(initial = null).value
    } else {
        null
    }
    val memberUiState by viewModel.memberUiState.collectAsState()
    val defaultMemberId = memberUiState.defaultMemberId

    var loadedFromAsset by remember(assetId) { mutableStateOf(assetId == null) }
    var name by remember(assetId) { mutableStateOf("") }
    var description by remember(assetId) { mutableStateOf("") }
    var purchaseValue by remember(assetId) { mutableStateOf("") }
    var isRetired by remember(assetId) { mutableStateOf(false) }
    var currency by remember(assetId) { mutableStateOf("CNY") }
    var purchaseDate by remember(assetId) { mutableStateOf(Formatters.formatDate(System.currentTimeMillis())) }
    var location by remember(assetId) { mutableStateOf("") }
    var notes by remember(assetId) { mutableStateOf("") }
    var imageUrl by remember(assetId) { mutableStateOf("") }
    var selectedCategory by remember(assetId) { mutableStateOf(AssetCategory.PHYSICAL.name) }
    var selectedMemberId by remember(assetId) { mutableStateOf<Long?>(null) }
    var errorMessage by remember(assetId) { mutableStateOf<String?>(null) }
    var searchingImage by remember(assetId) { mutableStateOf(false) }
    var saving by remember(assetId) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUrl = uri.toString()
            errorMessage = null
        }
    }

    LaunchedEffect(existingAsset?.id) {
        val asset = existingAsset ?: return@LaunchedEffect
        if (!loadedFromAsset) {
            name = asset.name
            description = asset.description
            purchaseValue = Formatters.formatNumber(asset.purchaseValue)
            isRetired = asset.isRetired
            currency = asset.currency
            purchaseDate = Formatters.formatDate(asset.purchaseDate)
            location = asset.location
            notes = asset.notes
            imageUrl = asset.imageUrl.orEmpty()
            selectedCategory = asset.category.name
            selectedMemberId = asset.memberId ?: defaultMemberId
            loadedFromAsset = true
        }
    }

    LaunchedEffect(defaultMemberId, assetId, loadedFromAsset) {
        if (assetId == null && selectedMemberId == null) {
            selectedMemberId = defaultMemberId
        }
        if (assetId != null && loadedFromAsset && selectedMemberId == null) {
            selectedMemberId = defaultMemberId
        }
    }

    Scaffold(
        topBar = {
            TelegramTopBar(
                title = if (assetId == null) "新增收藏" else "编辑收藏",
                subtitle = "记录你拥有过的每一件物品",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "记录物品名称、购入金额和买入时间，也可以从本地相册或公开接口补一张图片。",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("物品名称") },
                placeholder = { Text("例如：深圳公寓、LABUBU、某件收藏品") },
                singleLine = true
            )

            CategorySelector(
                selected = AssetCategory.valueOf(selectedCategory),
                onSelected = { selectedCategory = it.name }
            )

            MemberSelector(
                members = memberUiState.members,
                selectedMemberId = selectedMemberId,
                defaultMemberId = defaultMemberId,
                onSelected = { selectedMemberId = it }
            )

            StatusSelector(
                isRetired = isRetired,
                onStatusChanged = { isRetired = it }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("简介") },
                placeholder = { Text("补充型号、系列、系列编号或项目说明") }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = purchaseValue,
                    onValueChange = { purchaseValue = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("购入金额") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it.uppercase() },
                    modifier = Modifier.weight(1f),
                    label = { Text("币种") },
                    placeholder = { Text("CNY") },
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = purchaseDate,
                onValueChange = { purchaseDate = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("买入日期") },
                placeholder = { Text("2026-04-21") },
                singleLine = true
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("位置或来源") },
                placeholder = { Text("例如：上海、钱包地址、收藏柜") },
                singleLine = true
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("备注") },
                placeholder = { Text("记录租金情况、藏品状态、交易链接等") }
            )

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("物品图片", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "可以直接从本地相册选择，也可以输入名称后从公开可访问接口里找图，适合实物、项目名或常见 NFT 系列。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                imagePicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Rounded.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("从相册选择")
                        }
                        OutlinedButton(
                            onClick = {
                                if (name.isBlank()) {
                                    errorMessage = "请先输入物品名称"
                                    return@OutlinedButton
                                }
                                scope.launch {
                                    searchingImage = true
                                    errorMessage = null
                                    val foundUrl = viewModel.searchPublicImage(name)
                                    if (foundUrl.isNullOrBlank()) {
                                        errorMessage = "没有找到合适的公开图片"
                                    } else {
                                        imageUrl = foundUrl
                                    }
                                    searchingImage = false
                                }
                            },
                            enabled = !searchingImage,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (searchingImage) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Rounded.Search, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("搜索公开图片")
                        }
                    }
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("图片地址") },
                        placeholder = { Text("可手动粘贴 URL") }
                    )
                    AssetImageCard(
                        imageUrl = imageUrl.ifBlank { null },
                        category = AssetCategory.valueOf(selectedCategory)
                    )
                }
            }

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val parsedPurchase = purchaseValue.toDoubleOrNull()
                    val parsedDate = Formatters.parseDate(purchaseDate)

                    errorMessage = when {
                        name.isBlank() -> "物品名称不能为空"
                        parsedPurchase == null -> "请输入正确的购入金额"
                        parsedDate == null -> "买入日期格式需要是 YYYY-MM-DD"
                        else -> null
                    }

                    if (errorMessage != null) {
                        return@Button
                    }

                    scope.launch {
                        saving = true
                        viewModel.saveAsset(
                            existingAsset = existingAsset,
                            name = name,
                            category = AssetCategory.valueOf(selectedCategory),
                            description = description,
                            purchaseValue = parsedPurchase ?: 0.0,
                            isRetired = isRetired,
                            purchaseDate = parsedDate ?: System.currentTimeMillis(),
                            currency = currency,
                            imageUrl = imageUrl,
                            memberId = selectedMemberId ?: defaultMemberId,
                            location = location,
                            notes = notes
                        )
                        saving = false
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving,
                shape = RoundedCornerShape(10.dp)
            ) {
                if (saving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (assetId == null) "保存收藏" else "保存修改")
            }
        }
    }
}

@Composable
private fun MemberSelector(
    members: List<Member>,
    selectedMemberId: Long?,
    defaultMemberId: Long?,
    onSelected: (Long?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("归属成员", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        if (members.isEmpty()) {
            Text(
                text = "系统会自动创建默认成员",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(members, key = { it.id }) { member ->
                val effectiveSelectedId = selectedMemberId ?: defaultMemberId
                FilterChip(
                    selected = effectiveSelectedId == member.id,
                    onClick = { onSelected(member.id) },
                    label = { Text(if (member.isDefault) "${member.name}(默认)" else member.name) },
                    leadingIcon = {
                        MemberAvatar(
                            member = member,
                            size = 20.dp
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusSelector(isRetired: Boolean, onStatusChanged: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("当前状态", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = !isRetired,
                onClick = { onStatusChanged(false) },
                label = { Text("正在陪伴") }
            )
            FilterChip(
                selected = isRetired,
                onClick = { onStatusChanged(true) },
                label = { Text("已归档") }
            )
        }
    }
}

@Composable
private fun CategorySelector(selected: AssetCategory, onSelected: (AssetCategory) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("物品类型", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(AssetCategory.entries) { category ->
                FilterChip(
                    selected = category == selected,
                    onClick = { onSelected(category) },
                    label = { Text(category.displayName) },
                    leadingIcon = {
                        Icon(categoryIcon(category), contentDescription = null)
                    }
                )
            }
        }
    }
}

@Composable
private fun AssetImageCard(
    imageUrl: String?,
    category: AssetCategory,
    onClick: (() -> Unit)? = null,
    placeholderHint: String = "搜索结果会显示在这里"
) {
    Card(
        modifier = if (onClick != null) {
            Modifier.clickable(onClick = onClick)
        } else {
            Modifier
        },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = category.displayName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Rounded.ImageSearch,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("还没有图片", fontWeight = FontWeight.SemiBold)
                Text(
                    text = placeholderHint,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun categoryIcon(category: AssetCategory) = when (category) {
    AssetCategory.REAL_ESTATE -> Icons.Rounded.HomeWork
    AssetCategory.PHYSICAL -> Icons.Rounded.Inventory2
    AssetCategory.NFT -> Icons.Rounded.AutoAwesome
    AssetCategory.CRYPTO -> Icons.Rounded.CurrencyBitcoin
}

private data class CategoryAccent(val containerColor: Color, val contentColor: Color)

private fun categoryAccent(category: AssetCategory) = when (category) {
    AssetCategory.REAL_ESTATE -> CategoryAccent(
        containerColor = Color(0xFFE7F0FF),
        contentColor = Color(0xFF2D5DB3)
    )
    AssetCategory.PHYSICAL -> CategoryAccent(
        containerColor = StorixGreenLight,
        contentColor = Color(0xFF3D5F52)
    )
    AssetCategory.NFT -> CategoryAccent(
        containerColor = Color(0xFFF6E9FF),
        contentColor = Color(0xFF8650C7)
    )
    AssetCategory.CRYPTO -> CategoryAccent(
        containerColor = Color(0xFFFFF1D6),
        contentColor = Color(0xFF7A4A00)
    )
}

private fun buildSummaryMetrics(uiState: HomeUiState): List<SummaryMetricItem> {
    return buildList {
        add(SummaryMetricItem(title = "正在陪伴", value = "${uiState.activeAssetCount} 件"))
        add(SummaryMetricItem(title = "已归档", value = "${uiState.retiredAssetCount} 件"))
        uiState.longestCompanionAsset?.let {
            add(
                SummaryMetricItem(
                    title = "陪伴最久",
                    value = Formatters.formatHoldingPeriod(it.purchaseDate)
                )
            )
        }
        uiState.newestAsset?.let {
            add(SummaryMetricItem(title = "最新加入", value = it.name))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TelegramTopBar(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                }
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, fontWeight = FontWeight.SemiBold)
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = { actions() }
    )
}
