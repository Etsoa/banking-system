using System.Text.Json;
using System.Text.Json.Serialization;

namespace ServeurCompteDepot.Config
{
    public class DateTimeConverter : JsonConverter<DateTime>
    {
        public override DateTime Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            return DateTime.Parse(reader.GetString()!);
        }

        public override void Write(Utf8JsonWriter writer, DateTime value, JsonSerializerOptions options)
        {
            // Format avec millisecondes pour compatibilité Java
            writer.WriteStringValue(value.ToString("yyyy-MM-ddTHH:mm:ss.fff"));
        }
    }
}