package ru.graviton.profiles.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.graviton.profiles.dto.ClientDto;
import ru.graviton.profiles.dto.response.ResultResponse;
import ru.graviton.profiles.dto.response.VoidResultResponse;
import ru.graviton.profiles.service.ClientService;

import java.util.UUID;

@RestController
@RequestMapping("client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping("/about/{uid}")
    public ResultResponse<ClientDto> getClientInformation(@PathVariable("uid") UUID uid) {
        return ResultResponse.of(clientService.getClient(uid));
    }

    @PostMapping
    public VoidResultResponse saveClientInformation(@RequestBody ClientDto clientDto) {
        clientService.addClient(clientDto);
        return new VoidResultResponse();
    }

}
