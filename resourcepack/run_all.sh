echo "Running all resource pack generation scripts"
python3 cable_generator.py
python3 cable_generator_models.py
python3 energy_cube_generator.py
python3 energy_cube_generator_models.py
python3 energy_indicator_generator.py
python3 zip.py
echo "Resource pack generation is finished"