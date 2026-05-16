package com.danish.noorservice.data.model

import com.google.firebase.firestore.PropertyName

data class Proposal(
    val id: String = "",
    val proposalType: String = "",
    val status: String = "pending",
    val sentAt: Long = System.currentTimeMillis(),

    @PropertyName("euid") val eUid: String = "",
    @PropertyName("efullName") val eFullName: String = "",
    @PropertyName("eemail") val eEmail: String = "",
    @PropertyName("ephone") val ePhone: String = "",
    @PropertyName("ecity") val eCity: String = "",
    @PropertyName("earea") val eArea: String = "",
    @PropertyName("eaddress") val eAddress: String = "",
    @PropertyName("ephotoUrl") val ePhotoUrl: String = "",

    @PropertyName("wid") val wId: String = "",
    @PropertyName("wname") val wName: String = "",
    @PropertyName("winitials") val wInitials: String = "",
    @PropertyName("wavatarColorValue") val wAvatarColorValue: Int = 0,
    @PropertyName("wphotoUrl") val wPhotoUrl: String = "",
    @PropertyName("wusername") val wUsername: String = "",
    @PropertyName("wcity") val wCity: String = "",
    @PropertyName("warea") val wArea: String = "",
    @PropertyName("wphone") val wPhone: String = "",
    @PropertyName("wemail") val wEmail: String = "",
    @PropertyName("wcnic") val wCnic: String = "",
    @PropertyName("wdob") val wDob: String = "",
    @PropertyName("wgender") val wGender: String = "",
    @PropertyName("waddress") val wAddress: String = "",
    @PropertyName("wserviceIds") val wServiceIds: List<String> = emptyList(),
    @PropertyName("wskills") val wSkills: List<String> = emptyList(),
    @PropertyName("wlanguages") val wLanguages: List<String> = emptyList(),
    @PropertyName("wexperience") val wExperience: String = "",
    @PropertyName("wlicenceType") val wLicenceType: String = "",
    @PropertyName("wavailableDays") val wAvailableDays: List<String> = emptyList(),
    @PropertyName("wtimeSlot") val wTimeSlot: String = "",
    @PropertyName("wadditionalNote") val wAdditionalNote: String = "",
    @PropertyName("wisAvailable") val wIsAvailable: Boolean = true,
    @PropertyName("wjoinedDate") val wJoinedDate: String = "",
    @PropertyName("wdailyRate") val wDailyRate: String = "",
    @PropertyName("whourlyRate") val wHourlyRate: String = "",
    @PropertyName("wmonthlyRate") val wMonthlyRate: String = "",
    @PropertyName("wbio") val wBio: String = "",
    @PropertyName("wservices") val wServices: List<WorkerServiceDetail> = emptyList(),

    @PropertyName("vid") val vId: String = "",
    @PropertyName("vbusinessName") val vBusinessName: String = "",
    @PropertyName("vcontactPerson") val vContactPerson: String = "",
    @PropertyName("vphone") val vPhone: String = "",
    @PropertyName("vemail") val vEmail: String = "",
    @PropertyName("vntn") val vNtn: String = "",
    @PropertyName("vregNumber") val vRegNumber: String = "",
    @PropertyName("vcity") val vCity: String = "",
    @PropertyName("vaddress") val vAddress: String = "",
    @PropertyName("vlogoUrl") val vLogoUrl: String = "",
    @PropertyName("vbio") val vBio: String = "",
    @PropertyName("voperatingCities") val vOperatingCities: List<String> = emptyList(),
    @PropertyName("vserviceScale") val vServiceScale: String = "",
    @PropertyName("vyearsInBusiness") val vYearsInBusiness: Int = 0,
    @PropertyName("visoCertified") val vIsoCertified: Boolean = false,
    @PropertyName("vnotableClients") val vNotableClients: List<String> = emptyList(),
    @PropertyName("vheadOffice") val vHeadOffice: String = "",
    @PropertyName("vworkforceScale") val vWorkforceScale: String = "",
    @PropertyName("vservices") val vServices: List<VendorServiceDetail> = emptyList(),

    @PropertyName("serviceLabel") val serviceLabel: String = "",
    @PropertyName("jobTitle") val jobTitle: String = "",
    @PropertyName("location") val location: String = "",
    @PropertyName("schedule") val schedule: String = "",
    @PropertyName("startDate") val startDate: String = "",
    @PropertyName("offerPrice") val offerPrice: String = "",
    @PropertyName("note") val note: String = ""
)

data class WorkerServiceDetail(
    @PropertyName("serviceId") val serviceId: String = "",
    @PropertyName("skills") val skills: List<String> = emptyList(),
    @PropertyName("experience") val experience: String = "",
    @PropertyName("availabilityDays") val availabilityDays: List<String> = emptyList(),
    @PropertyName("availabilityTime") val availabilityTime: String = "",
    @PropertyName("additionalNote") val additionalNote: String = "",
    @PropertyName("dailyRate") val dailyRate: String = ""
)

data class VendorServiceDetail(
    @PropertyName("serviceId") val serviceId: String = "",
    @PropertyName("pricingModel") val pricingModel: String = "",
    @PropertyName("priceRange") val priceRange: String = "",
    @PropertyName("minContractDuration") val minContractDuration: String = "",
    @PropertyName("coverageAreas") val coverageAreas: List<String> = emptyList(),
    @PropertyName("isActive") val isActive: Boolean = true,
    @PropertyName("description") val description: String = "",
    @PropertyName("highlights") val highlights: List<String> = emptyList(),
    @PropertyName("skills") val skills: List<String> = emptyList()
)