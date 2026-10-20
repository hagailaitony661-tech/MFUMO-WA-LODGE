import streamlit as st
import pandas as pd
from datetime import datetime, date, timedelta

st.set_page_config(
    page_title="Mfumo wa Usimamizi wa Lodgi | Lodge Management System",
    page_icon="🏨",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Custom Styling
st.markdown("""
<style>
    .metric-card {
        background-color: #ffffff;
        border-radius: 12px;
        padding: 16px;
        border-left: 5px solid #1E40AF;
        box-shadow: 0 2px 6px rgba(0,0,0,0.06);
    }
    .tra-receipt {
        font-family: 'Courier New', monospace;
        background-color: #ffffff;
        padding: 20px;
        border: 2px dashed #64748b;
        border-radius: 8px;
        max-width: 380px;
        margin: 0 auto;
        color: #0f172a;
    }
    .boss-invoice {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background-color: #ffffff;
        padding: 30px;
        border: 1px solid #cbd5e1;
        border-radius: 12px;
        max-width: 680px;
        margin: 0 auto;
        color: #0f172a;
        box-shadow: 0 4px 15px rgba(0,0,0,0.05);
    }
</style>
""", unsafe_allow_html=True)

# State initialization (CLEAN COMMERCIAL STATE - ZERO DEMO DATA)
if "business" not in st.session_state:
    st.session_state.business = {
        "name": "LODGE & SUITES MANAGEMENT",
        "slogan": "Ukarimu, Utulivu na Usalama Masaa 24",
        "phone": "0754 000 000",
        "whatsapp": "+255 754 000 000",
        "email": "info@lodgemanager.co.tz",
        "tin": "100-000-000",
        "address": "Mtaa wa Posta, Mkoani, Tanzania"
    }

if "staff" not in st.session_state:
    st.session_state.staff = [
        {"jina": "Mhudumu wa Mapokezi", "simu": "0754 000 001", "wadhifa": "Mapokezi (Receptionist)", "shift": "Asubuhi (06:00 - 14:00)", "hali": "Kazini"},
        {"jina": "Meneja wa Zamu", "simu": "0754 000 002", "wadhifa": "Meneja wa Lodgi", "shift": "Mchana (14:00 - 22:00)", "hali": "Kazini"},
        {"jina": "Mlinzi wa Usiku", "simu": "0754 000 003", "wadhifa": "Mlinzi wa Usiku", "shift": "Usiku (22:00 - 06:00)", "hali": "Kazini"},
        {"jina": "Mhudumu wa Usafi", "simu": "0754 000 004", "wadhifa": "Mhudumu wa Usafi", "shift": "Asubuhi (06:00 - 14:00)", "hali": "Kazini"}
    ]

# All rooms start VACANT (Wazi)
if "rooms" not in st.session_state:
    st.session_state.rooms = [
        {"namba": "101", "aina": "Single", "bei": 35000, "hali": "Wazi", "sakafu": "Ghorofa ya 1"},
        {"namba": "102", "aina": "Single", "bei": 35000, "hali": "Wazi", "sakafu": "Ghorofa ya 1"},
        {"namba": "103", "aina": "Double", "bei": 60000, "hali": "Wazi", "sakafu": "Ghorofa ya 1"},
        {"namba": "201", "aina": "Double", "bei": 60000, "hali": "Wazi", "sakafu": "Ghorofa ya 2"},
        {"namba": "202", "aina": "VIP", "bei": 120000, "hali": "Wazi", "sakafu": "Ghorofa ya 2"},
        {"namba": "301", "aina": "VIP Suite", "bei": 180000, "hali": "Wazi", "sakafu": "Ghorofa ya 3"},
    ]

# Zero demo bookings and zero demo payments
if "bookings" not in st.session_state:
    st.session_state.bookings = []

if "payments" not in st.session_state:
    st.session_state.payments = []

# Sidebar Navigation
st.sidebar.title(f"🏨 {st.session_state.business['name']}")
st.sidebar.caption(st.session_state.business['slogan'])
menu = st.sidebar.radio(
    "Menyu Kuu / Navigation:",
    [
        "📊 Dashboard & Grafu",
        "🛏️ Usimamizi wa Vyumba",
        "🔑 Usajili (Check-in / Check-out)",
        "👥 Waajiriwa na Shift",
        "🧾 Risiti Ndogo & Invoice ya Boss",
        "⚙️ Mipangilio ya Ofisi & Logo"
    ]
)

st.sidebar.divider()
st.sidebar.markdown(f"**📞 Simu:** {st.session_state.business['phone']}")
st.sidebar.markdown(f"**📍 Mahali:** {st.session_state.business['address']}")

# 1. DASHBOARD
if menu == "📊 Dashboard & Grafu":
    st.title("📊 Dashboard ya Muhtasari wa Lodgi")
    st.write(f"Tarehe ya Leo: **{datetime.now().strftime('%d/%m/%Y')}**")

    total_rooms = len(st.session_state.rooms)
    occupied_rooms = sum(1 for r in st.session_state.rooms if r["hali"] == "Kimechukuliwa")
    vacant_rooms = sum(1 for r in st.session_state.rooms if r["hali"] == "Wazi")
    today_str = str(date.today())
    today_income = sum(p["kiasi"] for p in st.session_state.payments if p.get("tarehe") == today_str)

    c1, c2, c3, c4 = st.columns(4)
    c1.metric("Jumla ya Vyumba", total_rooms)
    c2.metric("Vilivyochukuliwa", occupied_rooms, f"{occupied_rooms}/{total_rooms}")
    c3.metric("Vilivyo Wazi", vacant_rooms)
    c4.metric("Mapato ya Leo", f"TZS {today_income:,.0f}/=")

    # EXCEL EXPORT BUTTON
    if st.session_state.payments:
        df_p = pd.DataFrame(st.session_state.payments)
        csv_data = df_p.to_csv(index=False).encode('utf-8')
        st.download_button(
            label="📥 Pakua Ripoti ya Mapato (Excel / CSV)",
            data=csv_data,
            file_name=f"Ripoti_ya_Mapato_{datetime.now().strftime('%Y%m%d')}.csv",
            mime="text/csv",
            type="primary"
        )

    st.divider()

    # CHARTS (GRAFU ZA MAPATO NA HALI YA VYUMBA)
    st.subheader("📈 Grafu na Takwimu za Mfumo")
    cg1, cg2 = st.columns(2)
    with cg1:
        st.markdown("**Vyumba Vilivyo Wazi vs Vilivyochukuliwa**")
        status_counts = pd.DataFrame({
            "Hali ya Chumba": ["Wazi", "Kimechukuliwa"],
            "Idadi": [vacant_rooms, occupied_rooms]
        }).set_index("Hali ya Chumba")
        st.bar_chart(status_counts)

    with cg2:
        st.markdown("**Mwenendo wa Malipo (TZS)**")
        if st.session_state.payments:
            df_pay = pd.DataFrame(st.session_state.payments)
            if "kiasi" in df_pay.columns:
                df_grouped = df_pay.groupby("tarehe")["kiasi"].sum()
                st.line_chart(df_grouped)
        else:
            st.info("Hakuna data za malipo bado. Grafu itaonekana pindi wateja wakisajiliwa!")

    st.divider()

    st.subheader("🗂️ Hali ya Vyumba")
    cols = st.columns(3)
    for idx, room in enumerate(st.session_state.rooms):
        col = cols[idx % 3]
        color = "🟢" if room["hali"] == "Wazi" else "🔴"
        with col:
            with st.container(border=True):
                st.markdown(f"### {color} Chumba {room['namba']}")
                st.write(f"**Aina:** {room['aina']} | **Ghorofa:** {room.get('sakafu', '1')}")
                st.write(f"**Bei kwa siku:** TZS {room['bei']:,}")
                st.write(f"**Hali:** **{room['hali']}**")

    st.divider()
    st.subheader("👥 Wageni Waliopo Ndani kwa Sasa")
    active_guests = [b for b in st.session_state.bookings if b["hali"] == "Yupo Ndani"]
    if active_guests:
        df_active = pd.DataFrame(active_guests)[["jina", "simu", "chumba", "origin", "destination", "malipo", "njia_ya_malipo", "staff"]]
        df_active.columns = ["Jina", "Simu", "Chumba", "Anakotoka", "Anakokwenda", "Kiasi (TZS)", "Njia ya Malipo", "Mhudumu"]
        st.dataframe(df_active, use_container_width=True)
    else:
        st.info("Hakuna mteja ndani kwa sasa. Mfumo uko tayari kupokea wageni wapya!")

# 2. VYUMBA
elif menu == "🛏️ Usimamizi wa Vyumba":
    st.title("🛏️ Usimamizi wa Vyumba")
    tab1, tab2 = st.tabs(["📋 Orodha ya Vyumba", "➕ Ongeza Chumba Kipya"])

    with tab1:
        df_rooms = pd.DataFrame(st.session_state.rooms)
        st.dataframe(df_rooms, use_container_width=True)

    with tab2:
        with st.form("new_room_form"):
            namba = st.text_input("Namba ya Chumba (mfano: 104, 203):")
            aina = st.selectbox("Aina ya Chumba:", ["Single", "Double", "VIP", "VIP Suite"])
            bei = st.number_input("Bei kwa Siku (TZS):", min_value=10000, step=5000, value=35000)
            sakafu = st.selectbox("Ghorofa:", ["Ghorofa ya Chini", "Ghorofa ya 1", "Ghorofa ya 2", "Ghorofa ya 3"])
            vifaa = st.text_input("Vifaa & Huduma (mfano: WiFi, AC, TV):", value="WiFi, AC, TV, Choo cha Ndani")
            if st.form_submit_button("Hifadhi Chumba"):
                if namba:
                    if any(r["namba"] == namba for r in st.session_state.rooms):
                        st.error("Namba hii ya chumba tayari ipo!")
                    else:
                        st.session_state.rooms.append({"namba": namba, "aina": aina, "bei": bei, "hali": "Wazi", "sakafu": sakafu, "vifaa": vifaa})
                        st.success(f"Chumba #{namba} kimehifadhiwa kikamilifu!")
                        st.rerun()

# 3. USAJILI (CHECK-IN / CHECK-OUT)
elif menu == "🔑 Usajili (Check-in / Check-out)":
    st.title("🔑 Usajili wa Wageni (Check-in & Check-out)")
    tab_in, tab_out = st.tabs(["📝 Sajili Mteja Mpya (Check-In)", "🚪 Wageni Waliopo Ndani & Check-Out"])

    with tab_in:
        vacant_list = [r for r in st.session_state.rooms if r["hali"] == "Wazi"]
        if not vacant_list:
            st.error("⚠️ Hakuna vyumba vilivyo wazi kwa sasa! Subiri mteja atoke (Check-out) au ongeza chumba kipya.")
        else:
            with st.form("checkin_form"):
                st.subheader("Taarifa Kamili za Mteja")
                c1, c2 = st.columns(2)
                with c1:
                    jina = st.text_input("Jina Kamili la Mteja *:")
                    simu = st.text_input("Namba ya Simu *:")
                    nida = st.text_input("Namba ya Kitambulisho / NIDA / Pasipoti *:")
                with c2:
                    origin = st.text_input("Anakotoka (Origin) *:", placeholder="mfano: Dar es Salaam, Dodoma...")
                    destination = st.text_input("Anakokwenda (Destination) *:", placeholder="mfano: Mbeya, Mwanza...")
                    staff_names = [s["jina"] for s in st.session_state.staff]
                    selected_staff = st.selectbox("Mhudumu wa Mapokezi:", staff_names if staff_names else ["Mhudumu"])

                st.subheader("Uteuzi wa Chumba na Malipo")
                room_options = {f"Chumba #{r['namba']} - {r['aina']} (TZS {r['bei']:,}/siku)": r for r in vacant_list}
                chosen_room_key = st.selectbox("Chagua Chumba Kilicho Wazi *:", list(room_options.keys()))
                chosen_room = room_options[chosen_room_key]

                c3, c4 = st.columns(2)
                with c3:
                    days = st.number_input("Idadi ya Siku za Kukaa *:", min_value=1, value=1)
                with c4:
                    njia = st.selectbox("Njia ya Malipo *:", ["Cash (Pesa Taslimu)", "M-Pesa", "Tigo Pesa", "Airtel Money", "HaloPesa", "Benki / Kadi"])

                total_cost = chosen_room["bei"] * days
                st.info(f"💰 **Jumla Kamili ya Gharama:** TZS {total_cost:,}/=")
                paid = st.number_input("Kiasi Kilicholipwa Sasa (TZS) *:", min_value=0, value=int(total_cost))

                if st.form_submit_button("Kamilisha Usajili (Check-In)"):
                    if not jina or not simu:
                        st.error("Tafadhali jaza Jina na Namba ya simu ya mteja!")
                    else:
                        new_booking = {
                            "id": int(datetime.now().timestamp()),
                            "jina": jina,
                            "simu": simu,
                            "kitambulisho": nida,
                            "origin": origin,
                            "destination": destination,
                            "staff": selected_staff,
                            "chumba": chosen_room["namba"],
                            "tarehe_kuingia": str(date.today()),
                            "siku": days,
                            "jumla": total_cost,
                            "malipo": paid,
                            "njia_ya_malipo": njia,
                            "hali": "Yupo Ndani"
                        }
                        st.session_state.bookings.append(new_booking)
                        for r in st.session_state.rooms:
                            if r["namba"] == chosen_room["namba"]:
                                r["hali"] = "Kimechukuliwa"
                        if paid > 0:
                            st.session_state.payments.append({
                                "tarehe": str(date.today()),
                                "mteja": jina,
                                "chumba": chosen_room["namba"],
                                "kiasi": paid,
                                "njia": njia,
                                "mhudumu": selected_staff,
                                "booking_id": new_booking["id"]
                            })
                        st.success(f"Mteja {jina} amesajiliwa kikamilifu katika Chumba #{chosen_room['namba']}! Risiti na Invoice ziko tayari kwenye menyu ya 'Risiti & Invoice'.")
                        st.rerun()

    with tab_out:
        active_list = [b for b in st.session_state.bookings if b["hali"] == "Yupo Ndani"]
        if not active_list:
            st.info("Hakuna mteja anayeishi ndani kwa sasa.")
        else:
            for b in active_list:
                with st.container(border=True):
                    c1, c2, c3 = st.columns([3, 2, 1.5])
                    with c1:
                        st.markdown(f"**Mteja:** {b['jina']} (Chumba #{b['chumba']})")
                        st.caption(f"Safari: {b.get('origin','-')} ➔ {b.get('destination','-')} | Simu: {b['simu']}")
                    with c2:
                        bal = b['jumla'] - b['malipo']
                        st.write(f"Jumla: TZS {b['jumla']:,} | Imelipwa: TZS {b['malipo']:,}")
                        if bal > 0:
                            st.markdown(f"<span style='color:red; font-weight:bold;'>Baki: TZS {bal:,}</span>", unsafe_allow_html=True)
                        else:
                            st.markdown("<span style='color:green; font-weight:bold;'>Imelipwa Yote</span>", unsafe_allow_html=True)
                    with c3:
                        if st.button("Check-Out Sasa", key=f"out_{b['id']}"):
                            b["hali"] = "Ametoka"
                            for r in st.session_state.rooms:
                                if r["namba"] == b["chumba"]:
                                    r["hali"] = "Wazi"
                            st.success(f"Mteja {b['jina']} ametoka! Chumba #{b['chumba']} kimerudi kuwa Wazi.")
                            st.rerun()

# 4. WAAJIRIWA NA SHIFT
elif menu == "👥 Waajiriwa na Shift":
    st.title("👥 Usimamizi wa Waajiriwa na Shift za Wahudumu")

    col_m, col_a, col_n = st.columns(3)
    col_m.info(f"🌅 **Shift ya Asubuhi (06:00-14:00):** {sum(1 for s in st.session_state.staff if 'Asubuhi' in s['shift'])} Wahudumu")
    col_a.info(f"☀️ **Shift ya Mchana (14:00-22:00):** {sum(1 for s in st.session_state.staff if 'Mchana' in s['shift'])} Wahudumu")
    col_n.info(f"🌙 **Shift ya Usiku (22:00-06:00):** {sum(1 for s in st.session_state.staff if 'Usiku' in s['shift'])} Wahudumu")

    st.subheader("Orodha ya Wahudumu Wote")
    df_staff = pd.DataFrame(st.session_state.staff)
    st.dataframe(df_staff, use_container_width=True)

    with st.expander("➕ Ongeza Mhudumu Mpya"):
        with st.form("add_staff_form"):
            s_name = st.text_input("Jina la Mhudumu:")
            s_phone = st.text_input("Namba ya Simu:")
            s_role = st.selectbox("Wadhifa:", ["Mapokezi (Receptionist)", "Meneja wa Lodgi", "Mhudumu wa Usafi", "Mlinzi wa Usiku", "Mhasibu"])
            s_shift = st.selectbox("Shift ya Kazi:", ["Asubuhi (06:00 - 14:00)", "Mchana (14:00 - 22:00)", "Usiku (22:00 - 06:00)"])
            if st.form_submit_button("Hifadhi Mhudumu"):
                if s_name:
                    st.session_state.staff.append({"jina": s_name, "simu": s_phone, "wadhifa": s_role, "shift": s_shift, "hali": "Kazini"})
                    st.success("Mhudumu ameongezwa kikamilifu!")
                    st.rerun()

# 5. RISITI NA INVOICE YA BOSS
elif menu == "🧾 Risiti Ndogo & Invoice ya Boss":
    st.title("🧾 Risiti za TRA & Invoice ya Boss")

    if not st.session_state.bookings:
        st.info("Hakuna kumbukumbu za wateja bado. Sajili mteja wa kwanza ili kuona na kutoa Risiti au Invoice ya Boss!")
    else:
        b_opts = {f"{b['jina']} - Chumba #{b['chumba']} (TZS {b['jumla']:,})": b for b in st.session_state.bookings}
        chosen_b_key = st.selectbox("Chagua Mteja wa Kutoa Hati:", list(b_opts.keys()))
        b = b_opts[chosen_b_key]

        b_name = st.session_state.business['name']
        b_tin = st.session_state.business['tin']
        b_phone = st.session_state.business['phone']
        b_address = st.session_state.business['address']
        b_slogan = st.session_state.business['slogan']
        b_email = st.session_state.business['email']
        balance = b['jumla'] - b['malipo']

        t1, t2 = st.tabs(["🧾 Risiti Ndogo ya Mashine (80mm POS Slip)", "📄 Invoice ya Boss (Official Statement & VAT)"])

        with t1:
            st.markdown(f"""
<div class="tra-receipt">
    <div style="text-align: center;">
        <h3 style="margin: 0; font-size: 15px;">{b_name}</h3>
        <p style="margin: 2px 0; font-size: 11px;">{b_slogan}</p>
        <p style="margin: 2px 0; font-size: 10px;">TIN: {b_tin}</p>
        <p style="margin: 2px 0; font-size: 10px;">{b_address} | {b_phone}</p>
        <p style="margin: 6px 0; font-weight: bold; border-top: 1px dashed #64748b; border-bottom: 1px dashed #64748b; padding: 2px 0;">*** RISITI YA FEDHA (EFD/POS) ***</p>
    </div>
    <div style="font-size: 11px; margin: 6px 0;">
        <p style="margin: 2px 0;"><b>RISITI NO:</b> #REC-{str(b['id'])[-4:]}</p>
        <p style="margin: 2px 0;"><b>TAREHE:</b> {datetime.now().strftime('%d/%m/%Y %H:%M')}</p>
        <p style="margin: 2px 0;"><b>MHUDUMU:</b> {b.get('staff', 'Mapokezi')}</p>
        <hr style="border-top: 1px dashed #64748b; margin: 4px 0;">
        <p style="margin: 2px 0;"><b>MTEJA:</b> {b['jina'].upper()}</p>
        <p style="margin: 2px 0;"><b>SIMU:</b> {b['simu']}</p>
        <p style="margin: 2px 0;"><b>NIDA/ID:</b> {b['kitambulisho']}</p>
        <p style="margin: 2px 0;"><b>SAFARI:</b> {b.get('origin', '-').upper()} ➔ {b.get('destination', '-').upper()}</p>
        <hr style="border-top: 1px dashed #64748b; margin: 4px 0;">
        <p style="margin: 2px 0;">CHUMBA #{b['chumba']} ({b['siku']} Siku): <b>TZS {b['jumla']:,}/=</b></p>
        <p style="margin: 2px 0; color: #15803d;"><b>IMELIPWA ({b['njia_ya_malipo']}): TZS {b['malipo']:,}/=</b></p>
        <p style="margin: 2px 0; color: {'#b91c1c' if balance > 0 else '#15803d'};"><b>BAKI: TZS {balance:,}/=</b></p>
    </div>
    <div style="text-align: center; margin-top: 8px; font-size: 10px;">
        <p style="margin: 0; font-weight: bold;">ASANTE KWA KUTUCHAGUA - KARIBU TENA</p>
        <span style="display:inline-block; background:#0f172a; color:#fff; padding:2px 6px; border-radius:3px; font-size:8px; margin-top:4px;">TRA VERIFIED RECEIPT</span>
    </div>
</div>
            """, unsafe_allow_html=True)

        with t2:
            st.markdown(f"""
<div class="boss-invoice">
    <div style="display: flex; justify-content: space-between; border-bottom: 2px solid #0f172a; padding-bottom: 12px; margin-bottom: 16px;">
        <div>
            <h2 style="margin: 0; font-size: 20px; font-weight: 800; color: #1e3a8a;">{b_name}</h2>
            <p style="margin: 2px 0; font-size: 12px; color: #64748b;">{b_slogan}</p>
            <p style="margin: 2px 0; font-size: 11px;">{b_address}</p>
            <p style="margin: 2px 0; font-size: 11px;">Simu: {b_phone} | Email: {b_email}</p>
            <p style="margin: 2px 0; font-size: 11px;"><b>TIN:</b> {b_tin}</p>
        </div>
        <div style="text-align: right;">
            <span style="background: #1e3a8a; color: white; padding: 4px 10px; border-radius: 4px; font-size: 11px; font-weight: bold;">TAX INVOICE</span>
            <h3 style="margin: 6px 0 2px 0; font-size: 16px; font-family: monospace;">#INV-2026-{str(b['id'])[-4:]}</h3>
            <p style="margin: 0; font-size: 11px; color: #64748b;">Tarehe: {datetime.now().strftime('%d/%m/%Y')}</p>
            <div style="margin-top: 6px;">
                <span style="display:inline-block; padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: bold; background: {'#dcfce7; color:#15803d;' if balance <= 0 else '#fee2e2; color:#b91c1c;'}">
                    {'IMELIPWA YOTE' if balance <= 0 else f'BAKI INAYODAIWA: TZS {balance:,}'}
                </span>
            </div>
        </div>
    </div>

    <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 12px; margin-bottom: 16px; font-size: 12px;">
        <b style="color: #475569; text-transform: uppercase;">BILLED TO / TAARIFA ZA MTEJA:</b><br>
        <span style="font-size: 14px; font-weight: bold;">{b['jina']}</span><br>
        Simu: {b['simu']} | Kitambulisho/NIDA: {b['kitambulisho']}<br>
        Safari: <b>{b.get('origin', '-')} ➔ {b.get('destination', '-')}</b> | Mhudumu: {b.get('staff', 'Mapokezi')}
    </div>

    <table style="width: 100%; border-collapse: collapse; font-size: 12px; margin-bottom: 16px;">
        <thead>
            <tr style="background: #f1f5f9; text-align: left;">
                <th style="padding: 8px; border-bottom: 1px solid #cbd5e1;">Maelezo ya Huduma</th>
                <th style="padding: 8px; border-bottom: 1px solid #cbd5e1; text-align: center;">Muda</th>
                <th style="padding: 8px; border-bottom: 1px solid #cbd5e1; text-align: right;">Jumla (TZS)</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td style="padding: 10px 8px; border-bottom: 1px solid #e2e8f0;">
                    <b>Malazi ya Chumba #{b['chumba']}</b><br>
                    <span style="font-size: 11px; color: #64748b;">Kiyoyozi (AC), WiFi, TV, Choo cha Ndani na Huduma</span>
                </td>
                <td style="padding: 10px 8px; border-bottom: 1px solid #e2e8f0; text-align: center;">{b['siku']} Usiku</td>
                <td style="padding: 10px 8px; border-bottom: 1px solid #e2e8f0; text-align: right; font-weight: bold;">TZS {b['jumla']:,}/=</td>
            </tr>
        </tbody>
    </table>

    <div style="display: flex; justify-content: flex-end; font-size: 12px; margin-bottom: 20px;">
        <div style="width: 250px;">
            <div style="display: flex; justify-content: space-between; padding: 2px 0;">
                <span>Gharama Halisi:</span> <span>TZS {b['jumla']:,}/=</span>
            </div>
            <div style="display: flex; justify-content: space-between; padding: 2px 0; color: #64748b;">
                <span>VAT (18%):</span> <span>Imejumuishwa</span>
            </div>
            <div style="display: flex; justify-content: space-between; padding: 4px 0; border-top: 1px solid #cbd5e1; border-bottom: 2px solid #0f172a; font-weight: bold; font-size: 13px;">
                <span>Jumla Kuu:</span> <span style="color:#1e3a8a;">TZS {b['jumla']:,}/=</span>
            </div>
            <div style="display: flex; justify-content: space-between; padding: 2px 0; color: #15803d; font-weight: bold;">
                <span>Imelipwa ({b['njia_ya_malipo']}):</span> <span>TZS {b['malipo']:,}/=</span>
            </div>
            <div style="display: flex; justify-content: space-between; padding: 2px 0; color: {'#b91c1c' if balance > 0 else '#64748b'}; font-weight: bold;">
                <span>Baki Inayodaiwa:</span> <span>TZS {balance:,}/=</span>
            </div>
        </div>
    </div>

    <div style="display: flex; justify-content: space-between; margin-top: 25px; border-top: 1px dashed #cbd5e1; padding-top: 15px; font-size: 11px; text-align: center;">
        <div style="width: 140px;">
            <p style="margin: 0; font-weight: bold;">{b.get('staff', 'Mapokezi')}</p>
            <div style="border-bottom: 1px solid #94a3b8; margin: 4px 0 2px 0;"></div>
            <span style="color: #64748b; font-size: 9px;">Sahihi ya Mhudumu</span>
        </div>
        <div style="width: 140px;">
            <p style="margin: 0; font-weight: bold;">Meneja / Boss</p>
            <div style="border-bottom: 1px solid #94a3b8; margin: 4px 0 2px 0;"></div>
            <span style="color: #64748b; font-size: 9px;">Muhuri na Sahihi</span>
        </div>
    </div>
</div>
            """, unsafe_allow_html=True)

# 6. MIPANGILIO YA OFISI
elif menu == "⚙️ Mipangilio ya Ofisi & Logo":
    st.title("⚙️ Mipangilio ya Ofisi, Logo na Mawasiliano")
    with st.form("biz_form"):
        st.session_state.business["name"] = st.text_input("Jina la Lodgi / Ofisi:", value=st.session_state.business["name"])
        st.session_state.business["slogan"] = st.text_input("Kaulimbiu / Slogan:", value=st.session_state.business["slogan"])
        st.session_state.business["phone"] = st.text_input("Namba za Simu za Ofisi:", value=st.session_state.business["phone"])
        st.session_state.business["whatsapp"] = st.text_input("Namba ya WhatsApp:", value=st.session_state.business["whatsapp"])
        st.session_state.business["email"] = st.text_input("Barua Pepe (Email):", value=st.session_state.business["email"])
        st.session_state.business["tin"] = st.text_input("Namba ya TIN (ya TRA):", value=st.session_state.business["tin"])
        st.session_state.business["address"] = st.text_input("Anuani / Mahali Ilipo:", value=st.session_state.business["address"])
        if st.form_submit_button("Hifadhi Mipangilio ya Biashara"):
            st.success("Taarifa za biashara zimehifadhiwa kikamilifu!")
            st.rerun()

    st.divider()
    st.subheader("🗑️ Sehemu ya Kusafisha Mfumo (Clear Demo Data for Sale)")
    st.caption("Tumia kitufe hiki kusafisha data zote za majaribio kabla ya kumkabidhi mteja wako!")
    if st.button("Futa Wateja na Malipo Yote (Clear All Data)", type="secondary"):
        st.session_state.bookings = []
        st.session_state.payments = []
        for r in st.session_state.rooms:
            r["hali"] = "Wazi"
        st.success("Mfumo umesafishwa! Vyumba vyote viko Wazi, 0 wateja na 0 malipo.")
        st.rerun()
