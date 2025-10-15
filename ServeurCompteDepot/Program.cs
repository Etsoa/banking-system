using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Enregistrement du DbContext avec PostgreSQL
builder.Services.AddDbContext<CompteDepotContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Enregistrement des services
builder.Services.AddScoped<ICompteService, CompteService>();

// Active la prise en charge des contrôleurs API
builder.Services.AddControllers();

var app = builder.Build();

// Middleware
app.UseHttpsRedirection();

// Mappe tous tes contrôleurs (ex: /api/CompteDepot/test)
app.MapControllers();

app.Run();
