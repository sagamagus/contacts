package com.alfredoguerrero.contacts.domain.usecases

import com.alfredoguerrero.contacts.framework.CaptureService

class CapturePhoto(val service: CaptureService): UseCase<Unit, Unit>() {
    override suspend fun execute(input: Unit) {
        if (service.isCameraPermissionGranted()){
            service.startCapture()
        }
    }
}