///api_version=2
(script = registerScript({
    name: "KillAuraPatch",
    authors: ["CzechHek"],
    version: "3.0"
})).import("Core.lib");

list = [
    rotationValues = [
        rotationspatch = new (Java.extend(BoolValue)) ("RotationsPatch", false) { onChanged: function () updateValues() },
        gcdsensitivity = value.createInteger("GCDSensitivity", mc.gameSettings.mouseSensitivity / 0.005, 0, 200)
    ],
    autoBlockValues1 = [
        autoblockpatch = new (Java.extend(BoolValue)) ("AutoBlockPatch", true) { onChanged: function () updateValues() },
        blockmode = new (Java.extend(ListValue)) ("BlockMode", ["Packet", "AfterTick", "LowHurtTime"], "LowHurtTime") { onChanged: function () updateValues() }
    ],
    reblockdelay = value.createInteger("ReblockDelay", 2, 0, 5),
    autoBlockValues2 = [
        blockrange = value.createFloat("BlockRange", 4.5, 3, 8),
        weaponcheck = value.createBoolean("WeaponCheck", true),
    ],
    autoBlockValues3 = [
        ownhurttimecheck = new (Java.extend(BoolValue)) ("OwnHurtTimeCheck", false) { onChanged: function () updateValues() },
        maxownhurttime = value.createInteger("MaxOwnHurtTime", 3, 0, 10)
    ],
    autoBlockValues4 = [
        facingcheck = new (Java.extend(BoolValue)) ("FacingCheck", true) { onChanged: function () updateValues() },
        toleration = value.createFloat("OffsetToleration", 0.5, 0, 2)
    ],
    criticalsValues = [
        hurttimecriticals = new (Java.extend(BoolValue)) ("HurtTimeCriticals", true) { onChanged: function () updateValues() },
        maxhurttimetojump = value.createInteger("MaxHurtTimeToJump", 4, 0, 10),
        jumpmode = value.createList("JumpMode", ["Legit", "SpeedModule"], "Legit")
    ],
    dynamicRangeValues = [
        dynamicrange = new (Java.extend(BoolValue)) ("DynamicRange", false) { onChanged: function () updateValues() },
        changeon = value.createList("ChangeOn", ["Attack", "Update"], "Attack"),
        minstillrange = new (Java.extend(FloatValue)) ("MinStillRange", 3, 3, 8) { onChanged: function (o, n) n > maxstillrange.get() && minstillrange.set(maxstillrange.get()) },
        maxstillrange = new (Java.extend(FloatValue)) ("MaxStillRange", 3.2, 3, 8) { onChanged: function (o, n) n < minstillrange.get() && maxstillrange.set(minstillrange.get()) },
        minwalkingrange = new (Java.extend(FloatValue)) ("MinWalkingRange", 3.4, 3, 8) { onChanged: function (o, n) n > maxwalkingrange.get() && minwalkingrange.set(maxwalkingrange.get()) },
        maxwalkingrange = new (Java.extend(FloatValue)) ("MaxWalkingRange", 3.6, 3, 8) { onChanged: function (o, n) n < minwalkingrange.get() && maxwalkingrange.set(minwalkingrange.get()) },
        minrunningrange = new (Java.extend(FloatValue)) ("MinRunningRange", 3.7, 3, 8) { onChanged: function (o, n) n > maxrunningrange.get() && minrunningrange.set(maxrunningrange.get()) },
        maxrunningrange = new (Java.extend(FloatValue)) ("MaxRunningRange", 3.9, 3, 8) { onChanged: function (o, n) n < minrunningrange.get() && maxrunningrange.set(minrunningrange.get()) },
        minjumpingrange = new (Java.extend(FloatValue)) ("MinJumpingRange", 3.7, 3, 8) { onChanged: function (o, n) n > maxjumpingrange.get() && minjumpingrange.set(maxjumpingrange.get()) },
        maxjumpingrange = new (Java.extend(FloatValue)) ("MaxJumpingRange", 3.9, 3, 8) { onChanged: function (o, n) n < minjumpingrange.get() && maxjumpingrange.set(minjumpingrange.get()) }
    ]
]

module = {
    category: "Patches",
    description: "KillAura addon that adds smart AutoBlock, forced GCD sensitivity and dynamic KillAura range.",
    values: list,
    onPacket: function (e) {
        if (rotationspatch.get() && KillAuraModule.target && e.getPacket() instanceof C03PacketPlayer && e.getPacket().getRotating()) {
            p = e.getPacket();
            m = 0.005 * gcdsensitivity.get();
            f = m * 0.6 + 0.2;
            gcd = m * m * m * 1.2;
            p.pitch -= p.pitch % gcd;
            p.yaw -= p.yaw % gcd;
        }
        if (e.getPacket() instanceof C02PacketUseEntity) unblock();
        else if (e.getPacket() instanceof C07PacketPlayerDigging && e.getPacket().getStatus() == "RELEASE_USE_ITEM") wasBlocking = false;

    },
    onUpdate: function () {
        reblockTimer.update();
        if (KillAuraModule.state) {
            autoblockpatch.get() && autoBlockValue.set(blockmode.get() == "LowHurtTime" ? "Off" : blockmode.get());
            dynamicrange.get() && changeon.get() == "Update" && updateRange();

            if (autoblockpatch.get() && mc.thePlayer.getHeldItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                var shouldBlock = false;
                if (!ownhurttimecheck.get() || mc.thePlayer.hurtTime <= maxownhurttime.get()) {
                    for each (var target in getTargetsInRange(blockrange.get())) {
                        if (!weaponcheck.get() || (target.getHeldItem() && (item = target.getHeldItem().getItem()) && (item instanceof ItemSword || item instanceof ItemAxe))) {
                            if (facingcheck.get()) {
                                if (target.rayTrace(blockrange.get(), 1).typeOfHit == "MISS") {
                                    eyesVec = target.getPositionEyes(1);
                                    lookVec = target.getLook(1);
                                    pointingVec = eyesVec.addVector(lookVec.xCoord * blockrange.get(), lookVec.yCoord * blockrange.get(), lookVec.zCoord * blockrange.get())
                                    border = mc.thePlayer.getCollisionBorderSize() + toleration.get();
                                    bb = mc.thePlayer.getEntityBoundingBox().expand(border, border, border);
                                    shouldBlock = !!bb.calculateIntercept(eyesVec, pointingVec) || bb.intersectsWith(target.getEntityBoundingBox());
                                }
                            } else shouldBlock = true;
                        }
                        
                        if (shouldBlock) break
                    }
                }

                autoBlockValue.set(!shouldBlock || blockmode.get() == "LowHurtTime" ? "Off" : blockmode.get());
                if (blockmode.get() == "LowHurtTime") {
                    if (shouldBlock) {
                        if (!wasBlocking && reblockTimer.hasTimePassed(reblockdelay.get() + 1)) {
                            sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0), true)
                            reblockTimer.reset();
                            wasBlocking = true;
                            mc.thePlayer.itemInUseCount = 1;
                        }
                    } else unblock();
                }
            }

            if (hurttimecriticals.get() && mc.thePlayer.onGround) {
                var targets = getTargetsInRange(rangeValue.get()), shouldJump = targets.some(function (e) e.hurtTime <= maxhurttimetojump.get());
                if (jumpmode.get() == "Legit") shouldJump && mc.thePlayer.jump();
                else if (targets.length) {
                    if (prevSpeed === null) prevSpeed = SpeedModule.state;
                    ARRAY_FIELD.set(SpeedModule, false);
                    STATE_FIELD.set(SpeedModule, shouldJump);
                    if (shouldJump) mc.thePlayer.movementInput.moveForward = 1;
                    return
                }
            }
        }

        if (mc.thePlayer.onGround && prevSpeed !== null) {
            STATE_FIELD.set(SpeedModule, prevSpeed);
            ARRAY_FIELD.set(SpeedModule, true);
            prevSpeed = null;
        }
    },
    onAttack: function () dynamicrange.get() && changeon.get() == "Attack" && updateRange(),
    onLoad: function () {
        autoBlockValue = KillAuraModule.getValue("AutoBlock");
        rangeValue = KillAuraModule.getValue("Range");
        hurtTimeValue = KillAuraModule.getValue("HurtTime");
    },
    onEnable: function () {
        prevHurtTime = hurtTimeValue.get();
        prevAutoBlock = autoBlockValue.get();
        prevRange = rangeValue.get();
        updateValues();
    },
    onDisable: function () {
        revertHurtTime();
        autoBlockValue.set(prevAutoBlock);
        rangeValue.set(prevRange);
        if (prevSpeed !== null) {
            SpeedModule.state = prevSpeed;
            prevSpeed = null;
            ARRAY_FIELD.set(SpeedModule, true);
        }
    },
    onClickGuiOpen: function () updateValues(),
    onClickGuiClosed: function () updateValues(true)
}

//To prevent config updates, sound effects, array animations
VALUE_FIELD = getField(Value, "value");
STATE_FIELD = getField(Module, "state");
ARRAY_FIELD = getField(Module, "array");
MAXIMUM_FIELD = getField(IntegerValue, "maximum");

Float = Java.type("java.lang.Float"),
reblockTimer = new TickTimer();

var wasBlocking, prevHurtTime, prevSpeed = null;



function revertHurtTime() {
    if (hurtTimeValue.getMaximum() == 3) {
        MAXIMUM_FIELD.set(hurtTimeValue, 10);
        hurtTimeValue.set(prevHurtTime);
    }
}

function updateValues(closing) {
    if (!closing) {
        if (autoblockpatch.get() && blockmode.get() == "LowHurtTime") {
            if (hurtTimeValue.getMaximum() > 3) {
                prevHurtTime = hurtTimeValue.get();
                MAXIMUM_FIELD.set(hurtTimeValue, 3);
                if (hurtTimeValue.get() > 3) hurtTimeValue.set(3);
            }
        } else if (hurtTimeValue.getMaximum() == 3) revertHurtTime();
    }

    setValues(KillAuraPatchModule, closing ? list : 
        [].concat(rotationspatch.get() ? rotationValues : rotationspatch)
        .concat(autoblockpatch.get() ? autoBlockValues1
            .concat(blockmode.get() == "LowHurtTime" ? [hurtTimeValue, reblockdelay] : null)
            .concat(autoBlockValues2)
            .concat(ownhurttimecheck.get() ? autoBlockValues3 : ownhurttimecheck)
            .concat(facingcheck.get() ? autoBlockValues4 : facingcheck)
        : autoblockpatch)
        .concat(hurttimecriticals.get() ? criticalsValues : hurttimecriticals)
        .concat(dynamicrange.get() ? dynamicRangeValues : dynamicrange))
} 

function updateRange() VALUE_FIELD.set(rangeValue, new Float(rand(isInputVertically(true) ? [minjumpingrange.get(), maxjumpingrange.get()] : isMovingHorizontally() ? mc.thePlayer.isSprinting() ? [minrunningrange.get(), maxrunningrange.get()] : [minwalkingrange.get(), maxwalkingrange.get()] : [minstillrange.get(), maxstillrange.get()])));

function unblock() {
    if (wasBlocking && blockmode.get() == "LowHurtTime") {
        sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN), true);
        wasBlocking = false
        mc.thePlayer.itemInUseCount = 0;
        reblockTimer.reset();
    }
}