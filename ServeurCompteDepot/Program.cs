using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Enregistrement du DbContext avec PostgreSQL
builder.Services.AddDbContext<CompteDepotContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Enregistrement des services
builder.Services.AddScoped<ICompteService, CompteService>();
builder.Services.AddScoped<ITransactionService, TransactionService>();
builder.Services.AddScoped<ITransfertService, TransfertService>();
builder.Services.AddScoped<IHistoriqueSoldeService, HistoriqueSoldeService>();
builder.Services.AddScoped<IHistoriqueStatutCompteService, HistoriqueStatutCompteService>();
builder.Services.AddScoped<ITypeTransactionService, TypeTransactionService>();
builder.Services.AddScoped<ITypeStatutCompteService, TypeStatutCompteService>();

// Active la prise en charge des contrôleurs API
builder.Services.AddControllers();

// Configuration CORS pour le centralizer
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowCentralizer", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

var app = builder.Build();

// Middleware
app.UseCors("AllowCentralizer");
app.UseHttpsRedirection();

// Mappe tous tes contrôleurs (ex: /api/CompteDepot/test)
app.MapControllers();

app.Run();
