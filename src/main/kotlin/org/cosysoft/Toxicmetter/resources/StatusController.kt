package org.cosysoft.Toxicmetter.resources

import org.cosysoft.Toxicmetter.models.Status
import org.cosysoft.Toxicmetter.services.ToxicService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
class StatusController(val toxicService: ToxicService) {
    @GetMapping("/status")
    fun getStatus(response: HttpServletResponse): Status {
        return Status(toxicService.getToxicLevel());
    }
}
