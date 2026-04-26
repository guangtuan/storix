package com.storix.app.data.repository

import android.net.Uri
import com.storix.app.data.local.Asset
import com.storix.app.data.local.AssetDao
import com.storix.app.data.local.Member
import com.storix.app.data.local.MemberDao
import com.storix.app.data.remote.PublicImageApi
import kotlinx.coroutines.flow.Flow

class AssetRepository(
    private val assetDao: AssetDao,
    private val memberDao: MemberDao,
    private val publicImageApi: PublicImageApi
) {
    fun observeAssets(): Flow<List<Asset>> = assetDao.observeAssets()

    fun observeAsset(assetId: Long): Flow<Asset?> = assetDao.observeAsset(assetId)

    fun observeMembers(): Flow<List<Member>> = memberDao.observeMembers()

    suspend fun upsert(asset: Asset) {
        assetDao.upsert(asset)
    }

    suspend fun delete(asset: Asset) {
        assetDao.delete(asset)
    }

    suspend fun getAllAssets(): List<Asset> = assetDao.getAllAssets()

    suspend fun replaceAssets(assets: List<Asset>) {
        assetDao.clearAll()
        if (assets.isNotEmpty()) {
            assetDao.upsertAll(assets)
        }
    }

    suspend fun upsertMember(member: Member) {
        memberDao.upsert(member)
    }

    suspend fun deleteMember(member: Member) {
        assetDao.clearMemberFromAssets(member.id)
        memberDao.delete(member)
    }

    suspend fun getAllMembers(): List<Member> = memberDao.getAllMembers()

    suspend fun replaceMembers(members: List<Member>) {
        memberDao.clearAll()
        if (members.isNotEmpty()) {
            memberDao.upsertAll(members)
        }
    }

    suspend fun ensureDefaultMember() {
        val defaultMember = memberDao.getDefaultMember()
        if (defaultMember != null) {
            return
        }

        val firstMember = memberDao.getFirstMember()
        if (firstMember == null) {
            val now = System.currentTimeMillis()
            memberDao.upsert(
                Member(
                    name = DefaultMemberName,
                    isDefault = true,
                    createdAt = now,
                    updatedAt = now
                )
            )
            return
        }

        memberDao.clearDefaultFlag()
        memberDao.setDefaultById(firstMember.id)
    }

    suspend fun setDefaultMember(memberId: Long) {
        memberDao.clearDefaultFlag()
        memberDao.setDefaultById(memberId)
    }

    suspend fun searchPublicImage(query: String): String? {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            return null
        }

        val languages = listOf("zh", "en")
        for (language in languages) {
            val title = runCatching {
                publicImageApi.search(searchUrl(language, normalizedQuery))
                    .query
                    ?.search
                    ?.firstOrNull()
                    ?.title
                    ?.takeIf { it.isNotBlank() }
            }.getOrNull() ?: continue

            val imageUrl = runCatching {
                val summary = publicImageApi.summary(summaryUrl(language, title))
                summary.originalImage?.source ?: summary.thumbnail?.source
            }.getOrNull()

            if (!imageUrl.isNullOrBlank()) {
                return imageUrl
            }
        }

        return null
    }

    private fun searchUrl(language: String, query: String): String {
        return "https://$language.wikipedia.org/w/api.php?action=query&list=search&srlimit=1&format=json&srsearch=${Uri.encode(query)}"
    }

    private fun summaryUrl(language: String, title: String): String {
        return "https://$language.wikipedia.org/api/rest_v1/page/summary/${Uri.encode(title)}"
    }

    companion object {
        const val DefaultMemberName = "默认成员"
    }
}
