package fr.pinguet62.jsfring.webservice;

import static fr.pinguet62.jsfring.webservice.RightWebservice.PATH;
import static java.util.Objects.requireNonNull;
import static org.springframework.core.convert.TypeDescriptor.collection;
import static org.springframework.core.convert.TypeDescriptor.valueOf;

import java.util.List;

import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.*;

import fr.pinguet62.jsfring.model.sql.Right;
import fr.pinguet62.jsfring.service.RightService;
import fr.pinguet62.jsfring.webservice.dto.RightDto;

@RestController
@RequestMapping(PATH)
public final class RightWebservice {

    public static final String PATH = "/right";

    private final ConversionService conversionService;

    private final RightService rightService;

    public RightWebservice(ConversionService conversionService, RightService rightService) {
        this.conversionService = requireNonNull(conversionService);
        this.rightService = requireNonNull(rightService);
    }

    @GetMapping("/{code}")
    public RightDto get(@PathVariable String code) {
        Right right = rightService.get(code);
        if (right == null)
            return null;
        return conversionService.convert(right, RightDto.class);
    }

    @GetMapping
    public List<RightDto> list() {
        List<Right> pojos = rightService.getAll();
        return (List<RightDto>) conversionService.convert(pojos, collection(List.class, valueOf(Right.class)),
                collection(List.class, valueOf(RightDto.class)));
    }

    @PostMapping
    public void update(@RequestBody RightDto rightDto) {
        Right right = conversionService.convert(rightDto, Right.class);
        rightService.update(right);
    }

}