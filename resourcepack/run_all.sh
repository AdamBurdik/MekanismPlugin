echo "Running all resource pack generation scripts"
./clean.sh
./prepare.sh
python3 cable_generator.py
python3 cable_generator_models.py
python3 energy_cube_generator.py
python3 energy_cube_generator_models.py
python3 energy_indicator_generator.py
python3 infuser_indicator_generator.py
python3 energy_vertical_indicator_generator.py
python3 slot_indicators_generator.py
python3 zip.py
echo "Resource pack generation is finished"
