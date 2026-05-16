package com.danish.noorservice.data.repository

import android.util.Log
import com.danish.noorservice.data.model.Proposal
import com.danish.noorservice.ui.screens.employer.AdminProposal
import com.danish.noorservice.ui.screens.employer.AdminProposalStatus
import com.danish.noorservice.ui.screens.employer.AdminProposalStore
import com.danish.noorservice.ui.screens.employer.VendorProposal
import com.danish.noorservice.ui.screens.employer.VendorProposalStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProposalRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun writeProposal(proposal: Proposal): Result<String> {
        return try {
            val id = if (proposal.id.isBlank()) {
                firestore.collection("proposals").document().id
            } else {
                proposal.id
            }
            val p = if (proposal.id.isBlank()) proposal.copy(id = id) else proposal
            Log.d("ProposalRepository", "writeProposal: eFullName=${p.eFullName}, ePhone=${p.ePhone}, eCity=${p.eCity}")
            firestore.collection("proposals").document(id).set(p).await()
            syncStoresFromFirestore()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProposalStatus(proposalId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("proposals").document(proposalId)
                .update("status", status).await()
            syncStoresFromFirestore()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProposalsByEmployer(employerId: String): List<Proposal> {
        return try {
            val docs = firestore.collection("proposals")
                .whereEqualTo("eUid", employerId)
                .get(Source.SERVER)
                .await()
            docs.documents.mapNotNull { it.toObject(Proposal::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllProposals(): List<Proposal> {
        return try {
            val docs = firestore.collection("proposals")
                .orderBy("sentAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get(Source.SERVER)
                .await()
            docs.documents.mapNotNull { it.toObject(Proposal::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProposalsByType(type: String): List<Proposal> {
        return try {
            val docs = firestore.collection("proposals")
                .whereEqualTo("proposalType", type)
                .orderBy("sentAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get(Source.SERVER)
                .await()
            docs.documents.mapNotNull { it.toObject(Proposal::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun syncStoresFromFirestore() {
        try {
            val docs = firestore.collection("proposals")
                .orderBy("sentAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get(Source.SERVER)
                .await()

            val workerProposals = mutableListOf<AdminProposal>()
            val vendorProposals = mutableListOf<VendorProposal>()

            for (doc in docs.documents) {
                val p = doc.toObject(Proposal::class.java) ?: continue
                val sentAtStr = formatTimestamp(p.sentAt)
                Log.d("ProposalRepository", "syncStoresFromFirestore: docId=${doc.id}, eFullName=${p.eFullName}, ePhone=${p.ePhone}, eCity=${p.eCity}")
                Log.d("ProposalRepository", "syncStoresFromFirestore: workerName=${p.wName}, workerUsername=${p.wUsername}")

                if (p.proposalType == "worker") {
                    workerProposals.add(
                        AdminProposal(
                            id = p.id,
                            workerName = p.wName,
                            workerUsername = p.wUsername,
                            workerInitials = p.wInitials,
                            workerPhone = p.wPhone,
                            avatarColor = androidx.compose.ui.graphics.Color(p.wAvatarColorValue),
                            jobTitle = p.jobTitle,
                            service = p.serviceLabel,
                            location = p.location,
                            schedule = p.schedule,
                            startDate = p.startDate,
                            offerPrice = p.offerPrice,
                            note = p.note,
                            sentAt = sentAtStr,
                            employerName = p.eFullName,
                            employerPhone = p.ePhone,
                            employerEmail = p.eEmail,
                            employerCity = p.eCity,
                            employerArea = p.eArea,
                            employerAddress = p.eAddress,
                            status = when (p.status) {
                                "accepted" -> AdminProposalStatus.ACCEPTED
                                "declined" -> AdminProposalStatus.DECLINED
                                else -> AdminProposalStatus.PENDING
                            },
                            workerCity = p.wCity,
                            workerArea = p.wArea,
                            workerPhoneFull = p.wPhone,
                            workerEmail = p.wEmail,
                            workerCnic = p.wCnic,
                            workerDob = p.wDob,
                            workerGender = p.wGender,
                            workerAddress = p.wAddress,
                            workerServiceIds = p.wServiceIds,
                            workerSkills = p.wSkills.ifEmpty { p.wServices.flatMap { it.skills } },
                            workerLanguages = p.wLanguages,
                            workerExperience = p.wExperience,
                            workerLicenceType = p.wLicenceType,
                            workerAvailableDays = p.wAvailableDays,
                            workerTimeSlot = p.wTimeSlot,
                            workerAdditionalNote = p.wAdditionalNote,
                            workerIsAvailable = p.wIsAvailable,
                            workerJoinedDate = p.wJoinedDate,
                            workerDailyRate = p.wDailyRate,
                            workerHourlyRate = p.wHourlyRate,
                            workerMonthlyRate = p.wMonthlyRate,
                            workerBio = p.wBio,
                            workerPhotoUrl = p.wPhotoUrl
                        )
                    )
                    Log.d("ProposalRepository", "syncStoresFromFirestore: AdminProposal created employerName=${p.eFullName}, workerName=${p.wName}")
                } else if (p.proposalType == "vendor") {
                    vendorProposals.add(
                        VendorProposal(
                            id = p.id,
                            vendorName = p.vBusinessName,
                            vendorEmoji = p.serviceLabel.substringBefore(" "),
                            vendorCity = p.vCity,
                            serviceLabel = p.serviceLabel,
                            jobTitle = p.jobTitle,
                            location = p.location,
                            schedule = p.schedule,
                            startDate = p.startDate,
                            budget = p.offerPrice,
                            note = p.note,
                            sentAt = sentAtStr,
                            employerName = p.eFullName,
                            employerPhone = p.ePhone,
                            employerEmail = p.eEmail,
                            employerCity = p.eCity,
                            employerArea = p.eArea,
                            employerAddress = p.eAddress,
                            status = when (p.status) {
                                "accepted" -> com.danish.noorservice.ui.screens.employer.VendorProposalStatus.ACCEPTED
                                "declined" -> com.danish.noorservice.ui.screens.employer.VendorProposalStatus.DECLINED
                                else -> com.danish.noorservice.ui.screens.employer.VendorProposalStatus.PENDING
                            },
                            vendorId = p.vId,
                            vendorContactPerson = p.vContactPerson,
                            vendorPhoneFull = p.vPhone,
                            vendorEmail = p.vEmail,
                            vendorNtn = p.vNtn,
                            vendorRegNumber = p.vRegNumber,
                            vendorAddress = p.vAddress,
                            vendorLogoUrl = p.vLogoUrl,
                            vendorBio = p.vBio,
                            vendorOperatingCities = p.vOperatingCities,
                            vendorServiceScale = p.vServiceScale,
                            vendorYearsInBusiness = p.vYearsInBusiness,
                            vendorIsoCertified = p.vIsoCertified,
                            vendorNotableClients = p.vNotableClients,
                            vendorHeadOffice = p.vHeadOffice,
                            vendorWorkforceScale = p.vWorkforceScale
                        )
                    )
                }
            }

            AdminProposalStore.proposals.clear()
            AdminProposalStore.proposals.addAll(workerProposals)
            VendorProposalStore.proposals.clear()
            VendorProposalStore.proposals.addAll(vendorProposals)
            Log.d("ProposalRepository", "syncStoresFromFirestore: done. workerProposals=${workerProposals.size}, vendorProposals=${vendorProposals.size}")
            Log.d("ProposalRepository", "syncStoresFromFirestore: first worker proposal employerName=${workerProposals.firstOrNull()?.employerName}")
        } catch (e: Exception) {
            // Silently fail — stores will keep existing data
        }
    }

    private fun formatTimestamp(ts: Long): String {
        return try {
            val sdf = java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault())
            sdf.format(java.util.Date(ts))
        } catch (e: Exception) {
            ""
        }
    }
}