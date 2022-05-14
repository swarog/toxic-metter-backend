package org.cosysoft.Toxicmetter.resources

import org.cosysoft.Toxicmetter.models.IncreaseRequest
import org.cosysoft.Toxicmetter.services.AlreadyVotedException
import org.cosysoft.Toxicmetter.services.ToxicService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class IncreaseResource(val toxicService: ToxicService) {

    @PostMapping("/increase")
    fun increaseToxic(@RequestBody increaseRequest: IncreaseRequest) {
        toxicService.increase(increaseRequest.deviceId)
    }

    @PostMapping("/reset")
    fun resetToxic() {
        toxicService.reset()
    }

}
