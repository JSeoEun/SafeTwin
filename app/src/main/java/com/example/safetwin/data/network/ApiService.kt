package com.example.safetwin.data.network

import com.example.safetwin.data.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ─────────────────────────────────────────────────────────────

    @POST("api/auth/signup")
    suspend fun signUp(@Body body: SignUpRequest): ApiResponse<TokenResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): ApiResponse<TokenResponse>

    @POST("api/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): ApiResponse<TokenResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Body body: LogoutRequest): ApiResponse<Unit>

    @POST("api/auth/biz-verify")
    suspend fun bizVerify(@Body body: BizVerifyRequest): ApiResponse<BizVerifyResponse>

    // ── Site ─────────────────────────────────────────────────────────────

    @POST("api/sites")
    suspend fun createSite(@Body body: CreateSiteRequest): ApiResponse<SiteResponse>

    @GET("api/sites")
    suspend fun getSites(): ApiResponse<List<SiteResponse>>

    @GET("api/sites/{id}")
    suspend fun getSite(@Path("id") id: Long): ApiResponse<SiteResponse>

    @PUT("api/sites/{id}")
    suspend fun updateSite(
        @Path("id") id: Long,
        @Body body: UpdateSiteRequest,
    ): ApiResponse<SiteResponse>

    @DELETE("api/sites/{id}")
    suspend fun deleteSite(@Path("id") id: Long): Response<Unit>

    // ── Worker ────────────────────────────────────────────────────────────

    @POST("api/sites/{siteId}/workers")
    suspend fun createWorker(
        @Path("siteId") siteId: Long,
        @Body body: CreateWorkerRequest,
    ): ApiResponse<WorkerResponse>

    @GET("api/sites/{siteId}/workers")
    suspend fun getWorkers(@Path("siteId") siteId: Long): ApiResponse<List<WorkerResponse>>

    @PUT("api/workers/{id}")
    suspend fun updateWorker(
        @Path("id") id: Long,
        @Body body: UpdateWorkerRequest,
    ): ApiResponse<WorkerResponse>

    @DELETE("api/workers/{id}")
    suspend fun deleteWorker(@Path("id") id: Long): Response<Unit>

    // ── Risk ──────────────────────────────────────────────────────────────

    @GET("api/risks")
    suspend fun getRisks(
        @Query("siteId") siteId: Long? = null,
        @Query("status") status: String? = null,
    ): ApiResponse<List<RiskResponse>>

    @PATCH("api/risks/{id}/status")
    suspend fun updateRiskStatus(
        @Path("id") id: Long,
        @Body body: UpdateRiskStatusRequest,
    ): ApiResponse<RiskResponse>

    // ── Analysis ──────────────────────────────────────────────────────────

    @Multipart
    @POST("api/analyses")
    suspend fun startAnalysis(
        @Query("zoneId") zoneId: Long,
        @Part image: MultipartBody.Part,
    ): ApiResponse<AnalysisResponse>

    @GET("api/analyses")
    suspend fun getAnalyses(
        @Query("siteId") siteId: Long? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): ApiResponse<PageResponse<AnalysisResponse>>

    @GET("api/analyses/{id}")
    suspend fun getAnalysis(@Path("id") id: Long): ApiResponse<AnalysisResponse>

    @GET("api/analyses/{id}/status")
    suspend fun getAnalysisStatus(@Path("id") id: Long): ApiResponse<AnalysisStatusResponse>

    @DELETE("api/analyses/{id}")
    suspend fun deleteAnalysis(@Path("id") id: Long): Response<Unit>

    // ── Docs ──────────────────────────────────────────────────────────────

    @POST("api/docs/risk-assessment")
    suspend fun createRiskAssessment(
        @Body body: CreateRiskAssessmentRequest,
    ): ApiResponse<DocResponse>

    @POST("api/docs/education-cert")
    suspend fun createEducationCert(
        @Body body: CreateEducationCertRequest,
    ): ApiResponse<DocResponse>

    @Multipart
    @POST("api/docs/{id}/group-photo")
    suspend fun attachGroupPhoto(
        @Path("id") id: Long,
        @Part photo: MultipartBody.Part,
    ): ApiResponse<DocResponse>

    @GET("api/docs")
    suspend fun getDocs(
        @Query("type") type: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): ApiResponse<PageResponse<DocResponse>>

    @Streaming
    @GET("api/docs/{id}/pdf")
    suspend fun downloadPdf(@Path("id") id: Long): Response<ResponseBody>

    @POST("api/docs/{id}/sign")
    suspend fun signDoc(
        @Path("id") id: Long,
        @Body body: SignRequest,
    ): ApiResponse<SignStatusResponse>

    @GET("api/docs/{id}/sign/status")
    suspend fun getSignStatus(@Path("id") id: Long): ApiResponse<SignStatusResponse>

    // ── Dashboard ─────────────────────────────────────────────────────────

    @GET("api/dashboard/summary")
    suspend fun getDashboardSummary(): ApiResponse<DashboardSummary>

    // ── Stats ─────────────────────────────────────────────────────────────

    @GET("api/stats/score-trend")
    suspend fun getScoreTrend(
        @Query("period") period: String = "monthly",
    ): ApiResponse<ScoreTrendResponse>

    @GET("api/stats/compare")
    suspend fun compareAnalyses(
        @Query("before") beforeId: Long,
        @Query("after") afterId: Long,
    ): ApiResponse<CompareResponse>

    // ── Topview ───────────────────────────────────────────────────────────

    @GET("api/topview/zones")
    suspend fun getZones(@Query("siteId") siteId: Long): ApiResponse<List<ZoneResponse>>

    @GET("api/topview/zones/{id}")
    suspend fun getZone(@Path("id") id: Long): ApiResponse<ZoneDetailResponse>

    @POST("api/topview/zones")
    suspend fun createZone(@Body body: CreateZoneRequest): ApiResponse<ZoneDetailResponse>

    @PUT("api/topview/zones/{id}")
    suspend fun updateZone(
        @Path("id") id: Long,
        @Body body: UpdateZoneRequest,
    ): ApiResponse<ZoneDetailResponse>

    @POST("api/topview/generate")
    suspend fun generateZones(
        @Body body: GenerateZoneRequest,
    ): ApiResponse<List<ZoneDetailResponse>>

    // ── Public Data ───────────────────────────────────────────────────────

    @GET("api/public-data/accident-cases")
    suspend fun getAccidentCases(
        @Query("industryType") industryType: String = "건설",
    ): ApiResponse<AccidentCasesResponse>

    @GET("api/public-data/laws")
    suspend fun getLaws(@Query("keyword") keyword: String): ApiResponse<LawsResponse>
}
