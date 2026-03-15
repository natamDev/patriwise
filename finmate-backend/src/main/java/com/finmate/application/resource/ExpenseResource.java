package com.finmate.application.resource;

import com.finmate.application.dto.CreateExpenseDto;
import com.finmate.application.dto.ExpenseDto;
import com.finmate.application.dto.UpdateExpenseDto;
import com.finmate.domain.model.Expense;
import com.finmate.domain.service.ExpenseService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Path("/api/expenses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
@Tag(name = "Expenses", description = "Expense tracking")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseResource {

    private final ExpenseService service;
    private final JsonWebToken jwt;

    public ExpenseResource(ExpenseService service, JsonWebToken jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @POST
    @Operation(summary = "Create expense")
    public Response create(@Valid CreateExpenseDto dto) {
        Expense expense = toModel(dto);
        Expense created = service.create(currentUserId(), expense);
        return Response.status(Response.Status.CREATED).entity(toDto(created)).build();
    }

    @GET
    @Operation(summary = "List expenses by month")
    public List<ExpenseDto> list(@QueryParam("month") String month) {
        YearMonth ym = month != null ? YearMonth.parse(month) : YearMonth.now();
        return service.findByMonth(currentUserId(), ym).stream().map(this::toDto).toList();
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Update expense")
    public Response update(@PathParam("id") UUID id, @Valid UpdateExpenseDto dto) {
        Expense patch = toPatchModel(dto);
        Expense updated = service.update(currentUserId(), id, patch);
        return Response.ok(toDto(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete expense")
    public Response delete(@PathParam("id") UUID id) {
        service.delete(currentUserId(), id);
        return Response.noContent().build();
    }

    private UUID currentUserId() {
        return UUID.fromString(jwt.getSubject());
    }

    // --- Mappers ---

    private Expense toModel(CreateExpenseDto dto) {
        Expense e = new Expense();
        e.setAmount(dto.getAmount());
        e.setCategory(dto.getCategory());
        e.setDescription(dto.getDescription());
        e.setExpenseDate(dto.getExpenseDate());
        return e;
    }

    private Expense toPatchModel(UpdateExpenseDto dto) {
        Expense e = new Expense();
        e.setAmount(dto.getAmount());
        e.setCategory(dto.getCategory());
        e.setDescription(dto.getDescription());
        e.setExpenseDate(dto.getExpenseDate());
        return e;
    }

    private ExpenseDto toDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setUserId(expense.getUserId());
        dto.setAmount(expense.getAmount());
        dto.setCategory(expense.getCategory());
        dto.setDescription(expense.getDescription());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setCreatedAt(expense.getCreatedAt());
        return dto;
    }
}
