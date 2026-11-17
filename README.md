# YhShop: Spawner & Enchants Edition

Shop GUI untuk **Paper 1.21.x (API 26.x)** khusus menjual:

- **Spawner** — via integrasi command ke **SmartSpawner** (`/ss give`)
- **Buku Enchant** — via integrasi command ke **ExcellentEnchants** (`/ee book`),
  termasuk 80+ enchant custom EE (daftar default dari EE 5.4.3) + 12 enchant vanilla.

Ekonomi: **Vault** (Essentials / EssentialsX sebagai provider). Balance pemain bisa
dari jual item antar pemain (AuctionHouse / QuickShop) atau quiz chat server.

## Install

1. Butuh terpasang: **Vault** + **EssentialsX** (economi), **SmartSpawner**, **ExcellentEnchants**.
2. Letakkan `YhShop-SpawnerEnchants.jar` di `plugins/`.
3. Restart / `reload`. Harga default di `spawners.yml` & `enchants.yml`
   sudah dikalkulasi dari quiz chat $50–200 (lihat komentar di `config.yml`).

## Pemakaian Player

- `/yhshop` — buka menu utama (2 section: **Spawner** & **Enchants**), klik untuk buka shop.
- `/yhshop spawner` — langsung buka shop spawner.
- `/yhshop enchant` — langsung buka shop enchant.
- Klik item di shop untuk membeli (otomatis tarik balance via Vault,
  item diberikan via command SmartSpawner/EE sebagai console).

## Admin

Permission: **`yhshop1.commands.admin`** (default: op).

- `/yhshop admin` — Admin GUI: browse semua entry, klik entry untuk detail.
  Edit **harga / level / nama / perintah give** atau **hapus** entry secara
  **realtime** (langsung tersimpan ke YAML, tanpa restart).
- Tombol **＋ Entry Baru** di Admin GUI: wizard di chat (ID → harga → level).
- `/yhshop admin setprice <spawner|enchant> <id> <harga>`
- `/yhshop admin givecmd <spawner|enchant> <id> <command...>`
- `/yhshop admin add <spawner|enchant> <id> <harga> [level]`
- `/yhshop admin remove <spawner|enchant> <id>`
- `/yhshop reload` — muat ulang config.

Saat edit lewat chat, ketik `batal` untuk membatalkan.

## Catatan Integrasi

- **SmartSpawner**: ID spawner harus sesuai nama di `spawner_mobs.yml`-nya
  (cek dengan `/ss list`). Template default: `ss give %player% %id% %amount%`.
- **ExcellentEnchants**: default ID tanpa namespace (EE 5.x). Kalau EE-mu v6+
  dan minta namespace, ubah template di `config.yml` jadi:
  `ee book excellentenchants:%id% %level% %player%`.
- Placeholder yang didukung di perintah give: `%player% %id% %level% %amount%`.
- Setiap entry bisa punya `give-command` sendiri (kosongkan untuk pakai template default).

## Menyeimbangkan Harga

Harga default mengikuti asumsi: quiz chat $50–200 + trading AuctionHouse/QuickShop.
Kalau servermu beda skalanya, edit harga di Admin GUI (realtime) atau langsung di
`spawners.yml` / `enchants.yml`, lalu `/yhshop reload`.

## License

MIT License - lihat file [LICENSE](LICENSE).
