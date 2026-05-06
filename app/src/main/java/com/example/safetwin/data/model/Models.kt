package com.example.safetwin.data.model

// ── 공통 래퍼 ──────────────────────────────────────────────────────────────

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
)

data class ApiError(
    val success: Boolean,
    val code: String,
    val message: String,
)

// ── Auth ──────────────────────────────────────────────────────────────────

data class TokenResponse(
    val tokenType: String,
    val accessToken: String,
    val refreshToken: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String? = null,
    val bizNumber: String? = null,
    val industry: String? = null,
    val companySize: String? = null,
)

data class RefreshRequest(val refreshToken: String)
data class LogoutRequest(val refreshToken: String)

data class BizVerifyRequest(val bizNumber: String)

data class BizVerifyResponse(
    val bizNumber: String,
    val valid: Boolean,
    val companyName: String?,
    val status: String?,
)

// ── Site ──────────────────────────────────────────────────────────────────

data class SiteResponse(
    val id: Long,
    val name: String,
    val address: String?,
    val bizNumber: String?,
    val status: String,
    val managerId: Long,
    val managerName: String,
    val createdAt: String,
)

data class CreateSiteRequest(
    val name: String,
    val address: String? = null,
    val bizNumber: String? = null,
)

data class UpdateSiteRequest(
    val name: String? = null,
    val address: String? = null,
    val status: String? = null,
)

// ── Worker ────────────────────────────────────────────────────────────────

data class WorkerResponse(
    val id: Long,
    val siteId: Long,
    val siteName: String,
    val name: String,
    val phone: String?,
    val occupation: String?,
    val startDate: String?,
    val endDate: String?,
    val createdAt: String,
)

data class CreateWorkerRequest(
    val name: String,
    val phone: String? = null,
    val occupation: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
)

data class UpdateWorkerRequest(
    val name: String? = null,
    val phone: String? = null,
    val occupation: String? = null,
    val endDate: String? = null,
)

// ── Risk ──────────────────────────────────────────────────────────────────

data class RiskResponse(
    val id: Long,
    val analysisId: Long,
    val siteId: Long,
    val siteName: String,
    val zoneLocation: String?,
    val label: String,
    val detail: String?,
    val level: String,
    val status: String,
    val law: String?,
    val action: String?,
    val x: Double?,
    val y: Double?,
    val createdAt: String,
)

data class UpdateRiskStatusRequest(val status: String)

// ── Analysis ──────────────────────────────────────────────────────────────

data class RiskItem(
    val id: Long,
    val label: String,
    val detail: String?,
    val level: String,
    val law: String?,
    val action: String?,
    val x: Double?,
    val y: Double?,
)

data class AnalysisResponse(
    val id: Long,
    val imageUrl: String?,
    val location: String?,
    val status: String,
    val analyzedAt: String?,
    val overallScore: Int?,
    val riskLevel: String?,
    val risks: List<RiskItem>,
)

data class AnalysisStatusResponse(
    val id: Long,
    val status: String,
)

// ── Pagination ────────────────────────────────────────────────────────────

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean,
)

// ── Docs ──────────────────────────────────────────────────────────────────

data class DocResponse(
    val id: Long,
    val title: String,
    val type: String,
    val status: String,
    val fileUrl: String?,
    val fileSize: Long?,
    val siteId: Long?,
    val siteName: String?,
    val analysisId: Long?,
    val createdAt: String,
)

data class CreateRiskAssessmentRequest(val analysisId: Long)

data class CreateEducationCertRequest(
    val siteId: Long,
    val educationDate: String,
    val content: String,
    val workerIds: List<Long>? = null,
)

data class SignRequest(
    val signerName: String,
    val signatureData: String,
)

data class SignatureInfo(
    val id: Long,
    val signerName: String,
    val signedAt: String,
)

data class SignStatusResponse(
    val documentId: Long,
    val documentStatus: String,
    val signatureCount: Int,
    val signatures: List<SignatureInfo>,
)

// ── Dashboard ─────────────────────────────────────────────────────────────

data class RecentAnalysis(
    val id: Long,
    val imageUrl: String?,
    val location: String?,
    val status: String,
    val overallScore: Int?,
    val riskLevel: String?,
    val analyzedAt: String?,
)

data class DashboardSummary(
    val safetyScore: Double,
    val safetyScoreDelta: Double,
    val weeklyRiskCount: Int,
    val weeklyRiskDelta: Int,
    val unresolvedCount: Int,
    val educationRate: Double,
    val safetyGrade: String,
    val tbmGuide: String?,
    val recentAnalyses: List<RecentAnalysis>,
)

// ── Stats ─────────────────────────────────────────────────────────────────

data class ScoreTrendResponse(
    val labels: List<String>,
    val scores: List<Int?>,
    val target: Int,
    val min: Int?,
    val current: Int?,
)

data class CompareResponse(
    val beforeId: Long,
    val afterId: Long,
    val beforeScore: Int,
    val afterScore: Int,
    val scoreDelta: Int,
    val beforeRiskCount: Int,
    val afterRiskCount: Int,
    val riskDelta: Int,
    val improvedItems: List<String>,
    val newItems: List<String>,
    val persistedItems: List<String>,
)

// ── Topview ───────────────────────────────────────────────────────────────

data class ZoneResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val floorNumber: Int?,
    val x: Double?,
    val y: Double?,
    val w: Double?,
    val h: Double?,
    val area: String?,
    val riskCount: Int,
    val riskLevel: String?,
)

data class RiskTag(
    val riskId: Long,
    val label: String,
    val x: Double?,
    val y: Double?,
    val law: String?,
    val accidentCase: String?,
)

data class ZoneDetailResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val floorNumber: Int?,
    val x: Double?,
    val y: Double?,
    val w: Double?,
    val h: Double?,
    val area: String?,
    val siteId: Long?,
    val siteName: String?,
    val riskCount: Int,
    val riskLevel: String?,
    val riskTags: List<RiskTag>,
)

data class CreateZoneRequest(
    val siteId: Long,
    val name: String,
    val description: String? = null,
    val floorNumber: Int? = null,
    val x: Double? = null,
    val y: Double? = null,
    val w: Double? = null,
    val h: Double? = null,
    val area: String? = null,
)

data class UpdateZoneRequest(
    val name: String? = null,
    val description: String? = null,
    val floorNumber: Int? = null,
    val x: Double? = null,
    val y: Double? = null,
    val w: Double? = null,
    val h: Double? = null,
    val area: String? = null,
)

data class GenerateZoneRequest(val analysisId: Long)

// ── Public Data ───────────────────────────────────────────────────────────

data class AccidentCase(
    val title: String,
    val year: Int,
    val cause: String?,
    val law: String?,
    val source: String?,
)

data class AccidentCasesResponse(
    val industryType: String,
    val cases: List<AccidentCase>,
)

data class LawItem(
    val title: String,
    val article: String,
    val content: String,
    val category: String?,
)

data class LawsResponse(
    val keyword: String,
    val laws: List<LawItem>,
)
