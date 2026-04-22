package com.storix.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.icons.rounded.HomeWork
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.storix.app.data.local.Asset
import com.storix.app.data.local.AssetCategory
import com.storix.app.ui.theme.TelegramBlueLight
import kotlinx.coroutines.launch

private const val HomeRoute = "home"
private const val DetailRoute = "detail"
private const val EditRoute = "edit"

private fun detailRoute(assetId: Long): String = "$DetailRoute/$assetId"
private fun editRoute(assetId: Long): String = "$EditRoute/$assetId"

@Composable
fun StorixApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        NavHost(navController = navController, startDestination = HomeRoute) {
            composable(HomeRoute) {
                HomeScreen(
                    viewModel = viewModel,
                    onAddAsset = { navController.navigate(EditRoute) },
                    onOpenAsset = { navController.navigate(detailRoute(it)) }
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
    onOpenAsset: (Long) -> Unit
) {
    val uiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        topBar = {
            TelegramTopBar(title = "Storix", subtitle = "资产总览")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAsset,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
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
                items(uiState.assets, key = { it.id }) { asset ->
                    AssetRowCard(asset = asset, onClick = { onOpenAsset(asset.id) })
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(uiState: HomeUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "资产原价",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f)
            )
            Text(
                text = Formatters.formatCurrency(uiState.totalOriginalCost, "CNY"),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryMetric(
                    title = "持有中",
                    value = uiState.activeAssetCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                SummaryMetric(
                    title = "已退役",
                    value = uiState.retiredAssetCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            SummaryMetric(
                title = "持有中原价",
                value = Formatters.formatCurrency(uiState.activeOriginalCost, "CNY"),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SummaryMetric(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun EmptyStateCard(onAddAsset: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
            Text("还没有资产记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = "先添加一条房产、实物或 NFT 资产，马上就能看到原价、状态和持有时长。",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onAddAsset, shape = RoundedCornerShape(14.dp)) {
                Text("现在添加")
            }
        }
    }
}

@Composable
private fun AssetRowCard(asset: Asset, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(TelegramBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(asset.category),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(asset.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = buildString {
                        append(asset.category.displayName)
                        append(" · ")
                        if (asset.isRetired) {
                            append("已退役 · ")
                        }
                        append("已持有 ")
                        append(Formatters.formatHoldingPeriod(asset.purchaseDate))
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = Formatters.formatCurrency(asset.purchaseValue, asset.currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "原价",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
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

    Scaffold(
        topBar = {
            TelegramTopBar(
                title = asset?.name ?: "资产详情",
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
                Text("未找到这条资产记录")
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
                    AssetImageCard(imageUrl = currentAsset.imageUrl, category = currentAsset.category)
                }
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
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
                            shape = RoundedCornerShape(18.dp),
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
            title = { Text("删除资产") },
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("核心指标", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            DetailLine(Icons.Rounded.Info, "原价", Formatters.formatCurrency(asset.purchaseValue, asset.currency))
            DetailLine(Icons.Rounded.Info, "使用状态", if (asset.isRetired) "已退役" else "使用中")
            DetailLine(Icons.Rounded.CalendarMonth, "买入日期", Formatters.formatDate(asset.purchaseDate))
            DetailLine(Icons.Rounded.CalendarMonth, "持有时长", Formatters.formatHoldingPeriod(asset.purchaseDate))
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
    var errorMessage by remember(assetId) { mutableStateOf<String?>(null) }
    var searchingImage by remember(assetId) { mutableStateOf(false) }
    var saving by remember(assetId) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
            loadedFromAsset = true
        }
    }

    Scaffold(
        topBar = {
            TelegramTopBar(
                title = if (assetId == null) "新增资产" else "编辑资产",
                subtitle = "像 Telegram 一样简洁填写信息",
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
                text = "记录名称、原价、使用状态和买入时间，还可以试着从公开接口查找图片。",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("资产名称") },
                placeholder = { Text("例如：深圳公寓、LABUBU、某件收藏品") },
                singleLine = true
            )

            CategorySelector(
                selected = AssetCategory.valueOf(selectedCategory),
                onSelected = { selectedCategory = it.name }
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
                    label = { Text("原价") },
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
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("公开图片", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "输入名称后尝试从公开可访问接口里找一张对应图片，适合实物、项目名或常见 NFT 系列。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                if (name.isBlank()) {
                                    errorMessage = "请先输入资产名称"
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
                            enabled = !searchingImage
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
                        name.isBlank() -> "资产名称不能为空"
                        parsedPurchase == null -> "请输入正确的原价"
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
                            location = location,
                            notes = notes
                        )
                        saving = false
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving,
                shape = RoundedCornerShape(14.dp)
            ) {
                if (saving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (assetId == null) "保存资产" else "保存修改")
            }
        }
    }
}

@Composable
private fun StatusSelector(isRetired: Boolean, onStatusChanged: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("使用状态", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = !isRetired,
                onClick = { onStatusChanged(false) },
                label = { Text("使用中") }
            )
            FilterChip(
                selected = isRetired,
                onClick = { onStatusChanged(true) },
                label = { Text("已退役") }
            )
        }
    }
}

@Composable
private fun CategorySelector(selected: AssetCategory, onSelected: (AssetCategory) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("资产类型", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
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
private fun AssetImageCard(imageUrl: String?, category: AssetCategory) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = category.displayName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(24.dp)),
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
                    text = "搜索结果会显示在这里",
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
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
                    )
                }
            }
        },
        actions = { actions() }
    )
}
