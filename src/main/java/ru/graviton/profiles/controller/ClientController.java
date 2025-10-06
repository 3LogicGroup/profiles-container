package ru.graviton.profiles.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.graviton.profiles.dto.ClientDto;
import ru.graviton.profiles.dto.response.ResultResponse;
import ru.graviton.profiles.service.ClientService;

@RestController
@RequestMapping("client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping("/about/{email}")
    public ResultResponse<ClientDto> getClientInformation(@PathVariable("email") String email) {
        return ResultResponse.of(clientService.getClient(email));
    }

    @PostMapping
    public ResultResponse<ClientDto> saveClientInformation(@RequestBody @Validated ClientDto clientDto) {
        return ResultResponse.of(clientService.addClient(clientDto));
    }

}
