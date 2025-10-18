using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using ServeurCompteDepot.Config;
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
builder.Services.AddScoped<IFraisService, FraisService>();

// Active la prise en charge des contrôleurs API
builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        // Convertisseur personnalisé pour DateTime avec millisecondes
        options.JsonSerializerOptions.Converters.Add(new DateTimeConverter());
        options.JsonSerializerOptions.Converters.Add(new System.Text.Json.Serialization.JsonStringEnumConverter());
        
        // Configuration pour gérer les références circulaires
        options.JsonSerializerOptions.ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles;
        options.JsonSerializerOptions.MaxDepth = 32;
    });

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
