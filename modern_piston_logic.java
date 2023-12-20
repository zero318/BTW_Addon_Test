private boolean moveBlocks(Level level, BlockPos blockPos, Direction direction, boolean is_extending) {
    BlockPos blockPos2 = blockPos.relative(direction);
    if (
        !is_extending &&
        level.getBlockState(blockPos2).is(Blocks.PISTON_HEAD)
    ) {
        level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), UPDATE_SUPPRESS_DROPS);
    }
    PistonStructureResolver pistonStructureResolver = new PistonStructureResolver(level, blockPos, direction, is_extending);
    if (!pistonStructureResolver.resolve()) {
        return false;
    }
    
    HashMap hashMap = Maps.newHashMap();
    List<BlockPos> push_list = pistonStructureResolver.getToPush();
    ArrayList arrayList = Lists.newArrayList();
    for (BlockPos arrblockState2 : push_list) {
        Object object = level.getBlockState(arrblockState2);
        arrayList.add(object);
        hashMap.put(arrblockState2, object);
    }
    List<BlockPos> destroy_list = pistonStructureResolver.getToDestroy();
    BlockState[] block_state_array = new BlockState[push_list.size() + destroy_list.size()];
    Direction movement_direction = is_extending ? direction : direction.getOpposite();
    int block_state_index = 0;
    for (int i = destroy_list.size() - 1; i >= 0; --i) {
        BlockPos blockPos3 = destroy_list.get(i);
        BlockState blockState = level.getBlockState(blockPos3);
        BlockEntity block_entity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos3) : null;
        PistonBaseBlock.dropResources(blockState, level, blockPos3, block_entity);
        level.setBlock(blockPos3, Blocks.AIR.defaultBlockState(), UPDATE_IMMEDIATE | UPDATE_KNOWN_SHAPE);
        level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos3, GameEvent.Context.of(blockState));
        if (!blockState.is(BlockTags.FIRE)) {
            level.addDestroyBlockEffect(blockPos3, blockState);
        }
        block_state_array[block_state_index++] = blockState;
    }
    for (int i = push_list.size() - 1; i >= 0; --i) {
        BlockPos blockPos3 = push_list.get(i);
        BlockState blockState = level.getBlockState(blockPos3);
        blockPos3 = blockPos3.relative(movement_direction);
        hashMap.remove(blockPos3);
        BlockState moving_piston_block_state = Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, direction);
        level.setBlock(blockPos3, moving_piston_block_state, UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
        level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(blockPos3, moving_piston_block_state, arrayList.get(i), direction, is_extending, false));
        block_state_array[block_state_index++] = blockState;
    }
    if (is_extending) {
        PistonType blockState = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
        BlockState piston_head_block_state = Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, direction).setValue(PistonHeadBlock.TYPE, blockState);
        BlockState moving_piston_block_state = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, direction).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
        hashMap.remove(blockPos2);
        level.setBlock(blockPos2, moving_piston_block_state, UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
        level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(blockPos2, moving_piston_block_state, piston_head_block_state, direction, true, true));
    }
    BlockState blockState = Blocks.AIR.defaultBlockState();
    for (BlockPos blockPos3 : hashMap.keySet()) {
        level.setBlock(blockPos3, blockState, UPDATE_CLIENTS | UPDATE_SUPPRESS_LIGHT);
    }
    for (Map.Entry entry : hashMap.entrySet()) {
        BlockPos blockPos3 = entry.getKey();
        BlockState blockState3 = entry.getValue();
        blockState3.updateIndirectNeighbourShapes(level, blockPos3, 2);
        blockState.updateNeighbourShapes(level, blockPos3, 2);
        blockState.updateIndirectNeighbourShapes(level, blockPos3, 2);
    }
    block_state_index = 0;
    for (int i = destroy_list.size() - 1; i >= 0; --i) {
        BlockState blockState4 = block_state_array[block_state_index++];
        BlockPos blockPos3 = destroy_list.get(i);
        blockState4.updateIndirectNeighbourShapes(level, blockPos3, 2);
        level.updateNeighborsAt(blockPos3, blockState4.getBlock());
    }
    for (int i = push_list.size() - 1; i >= 0; --i) {
        level.updateNeighborsAt(push_list.get(i), block_state_array[block_state_index++].getBlock());
    }
    if (is_extending) {
        level.updateNeighborsAt(blockPos2, Blocks.PISTON_HEAD);
    }
    return true;
}